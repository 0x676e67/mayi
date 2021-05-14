package com.zf1976.ant.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.power.common.util.ValidateUtil;
import com.zf1976.ant.common.component.load.annotation.CacheConfig;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.mail.ValidateFactory;
import com.zf1976.ant.common.component.mail.ValidateService;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.encrypt.MD5Encoder;
import com.zf1976.ant.common.encrypt.RsaUtil;
import com.zf1976.ant.common.encrypt.property.RsaProperties;
import com.zf1976.ant.upms.biz.property.FileProperties;
import com.zf1976.ant.upms.biz.convert.SysUserConvert;
import com.zf1976.ant.upms.biz.dao.SysDepartmentDao;
import com.zf1976.ant.upms.biz.dao.SysPositionDao;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
import com.zf1976.ant.upms.biz.exception.UserException;
import com.zf1976.ant.upms.biz.exception.enums.UserState;
import com.zf1976.ant.upms.biz.feign.SecurityClient;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateEmailDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdatePasswordDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.upms.biz.pojo.query.UserQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统用户(SysUser)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:46:01
 */
@Slf4j
@Service
@CacheConfig(namespace =  Namespace.USER, dependsOn = {Namespace.DEPARTMENT, Namespace.POSITION, Namespace.ROLE})
public class SysUserService extends AbstractService<SysUserDao, SysUser> {

    private final AlternativeJdkIdGenerator jdkIdGenerator = new AlternativeJdkIdGenerator();
    private final byte[] DEFAULT_PASSWORD_BYTE = "123456".getBytes(StandardCharsets.UTF_8);
    private final SysDepartmentDao sysDepartmentDao;
    private final SysPositionDao sysPositionDao;
    private final SysRoleDao sysRoleDao;
    private final SysUserConvert convert;
    private final SecurityClient securityClient;
    private final MD5Encoder encoder = new MD5Encoder();

    public SysUserService(SysDepartmentDao sysDepartmentDao, SysPositionDao sysJobDao, SysRoleDao sysRoleDao, SecurityClient securityClient) {
        this.securityClient = securityClient;
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysPositionDao = sysJobDao;
        this.sysRoleDao = sysRoleDao;
        this.convert = SysUserConvert.INSTANCE;
    }

    /**
     * 按条件分页查询用户
     *
     * @param query request page
     * @return /
     */
    @CachePut(key = "#query", dynamics = true)
    public IPage<UserVO> selectUserPage(Query<UserQueryParam> query) {
        IPage<SysUser> sourcePage;
        // 非super admin 过滤数据权限
        if (!SessionManagement.isOwner()) {
            // 用户可观察数据范围
            Set<Long> dataPermission = securityClient.getUserDetails().getData().getDataPermission();
            List<Long> userIds = super.baseMapper.selectByDepartmentIds(dataPermission);
            sourcePage = super.queryWrapper()
                              .chainQuery(query, () -> {
                                  // 自定义条件
                                  return ChainWrappers.queryChain(super.baseMapper)
                                                      .in(getColumn(SysUser::getId), userIds);
                              }).selectPage();
        } else {
            sourcePage = super.queryWrapper()
                              .chainQuery(query)
                              .selectPage();
        }
        // 根据部门分页
        IPage<SysUser> finalSourcePage = sourcePage;
        Optional.ofNullable(query.getQuery())
                .ifPresent(queryParam -> {
                    if (queryParam.getDepartmentId() != null) {
                        // 当前查询部门
                        Long departmentId = queryParam.getDepartmentId();
                        Set<Long> collectIds = new HashSet<>();
                        this.sysDepartmentDao.selectChildrenById(departmentId)
                                             .stream()
                                             .map(SysDepartment::getId)
                                             .forEach(id -> {
                                                 this.selectDepartmentTreeIds(id, collectIds);
                                             });
                        collectIds.add(departmentId);
                        List<SysUser> collectUser = finalSourcePage.getRecords()
                                                                   .stream()
                                                                   .filter(sysUser -> collectIds.contains(sysUser.getDepartmentId()))
                                                                   .collect(Collectors.toList());
                        finalSourcePage.setRecords(collectUser);
                    }
                });
        return super.mapPageToTarget(sourcePage, sysUser -> {
            ChainWrappers.lambdaQueryChain(this.sysDepartmentDao)
                         .select(SysDepartment::getId, SysDepartment::getName)
                         .eq(SysDepartment::getId, sysUser.getDepartmentId())
                         .oneOpt()
                         .ifPresent(sysUser::setDepartment);
            return this.convert.toVo(sysUser);
        });
    }

    /**
     * 获取当前部门树下所有id
     *
     * @param departmentId id
     * @param supplier supplier
     */
    public void selectDepartmentTreeIds(Long departmentId, Collection<Long> supplier){
        Assert.notNull(departmentId, "department id can not been null");
        supplier.add(departmentId);
        this.sysDepartmentDao.selectChildrenById(departmentId)
                             .stream()
                             .map(SysDepartment::getId)
                             .forEach(id -> {
                                 // 继续下一级子节点
                                 this.selectDepartmentTreeIds(id, supplier);
                             });
    }

    /**
     * 获取用户所有角色id
     *
     * @param id id
     * @return role id
     */
    public Set<Long> selectUserRoleIds(Long id) {
        return this.sysRoleDao.selectListByUserId(id)
                              .stream()
                              .filter(SysRole::getEnabled)
                              .map(SysRole::getId)
                              .collect(Collectors.toSet());
    }

    /**
     * 设置用户状态
     *
     * @param id      id
     * @param enabled enabled
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> setUserStatus(Long id, Boolean enabled) {
        SysUser sysUser = super.lambdaQuery()
                               .eq(SysUser::getId, id)
                               .oneOpt().orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
        sysUser.setEnabled(enabled);
        super.updateEntityById(sysUser);
        return Optional.empty();
    }

    /**
     * 获取用户职位id
     *
     * @param id id
     * @return position id
     */
    public Set<Long> selectUserPositionIds(Long id) {
        return this.sysPositionDao.selectListByUserId(id)
                                  .stream()
                                  .filter(SysPosition::getEnabled)
                                  .map(SysPosition::getId)
                                  .collect(Collectors.toSet());
    }

    /**
     * 修改头像
     *
     * @param multipartFile 上传头像
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateAvatar(MultipartFile multipartFile) {
        final Long sessionId = SessionManagement.getSessionId();
        final SysUser sysUser = super.lambdaQuery()
                                 .eq(SysUser::getId, sessionId)
                                 .one();
        try {
            final String oldFileName = sysUser.getAvatarName();
            final StringBuilder oldName = new StringBuilder(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            final StringBuilder freshName = oldName.insert(oldName.lastIndexOf("."), "-" + jdkIdGenerator.generateId());
            final Path path = Paths.get(FileProperties.getAvatarRealPath(), String.valueOf(freshName));
            Files.deleteIfExists(Paths.get(FileProperties.getAvatarRealPath(), oldFileName));
            multipartFile.transferTo(path);
            sysUser.setAvatarName(String.valueOf(freshName));
            super.updateEntityById(sysUser);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new BusinessException(BusinessMsgState.UPLOAD_ERROR);
        }
        return Optional.empty();
    }

    /**
     * 修改密码
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updatePassword(UpdatePasswordDTO dto) {
        final Long sessionId = SessionManagement.getSessionId();
        final SysUser sysUser = super.lambdaQuery()
                                     .eq(SysUser::getId, sessionId)
                                     .oneOpt().orElseThrow(() -> new BusinessException(BusinessMsgState.CODE_NOT_FOUNT));
        String rawPassword;
        String freshPassword;
        try {
            rawPassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getOldPass());
            freshPassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getNewPass());
        } catch (Exception e) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }

        if (StringUtils.isEmpty(rawPassword) || StringUtils.isEmpty(freshPassword)){
            throw new BusinessException(BusinessMsgState.NULL_PASSWORD);
        }

        if (rawPassword.equals(freshPassword)) {
            throw new BusinessException(BusinessMsgState.PASSWORD_REPEAT);
        }

        if (encoder.matches(rawPassword, sysUser.getPassword()) && !ValidateUtil.isPassword(freshPassword)) {
            sysUser.setPassword(encoder.encode(freshPassword));
            super.updateEntityById(sysUser);
            // 强制用户重新登陆
            SessionManagement.removeSession();
        }else {
            throw new BusinessException(BusinessMsgState.PASSWORD_LOW);
        }
        return Optional.empty();
    }

    /**
     * 修改邮箱
     *
     * @param code 验证码
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateEmail(String code, UpdateEmailDTO dto) {

        if (StringUtils.isEmpty(code) || ObjectUtils.isEmpty(dto)) {
            throw new BusinessException(BusinessMsgState.PARAM_ILLEGAL);
        }

        ValidateService validateService = ValidateFactory.getInstance();
        var sysUser = super.lambdaQuery()
                            .select(SysUser::getPassword)
                            .eq(SysUser::getId, SessionManagement.getSessionId())
                            .oneOpt()
                            .orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
        BusinessException businessException = null;
        if (validateService.validate(dto.getEmail(), code)) {
            try {
                String rawPassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getPassword());
                if (encoder.matches(rawPassword, sysUser.getPassword())) {
                    if (ObjectUtils.nullSafeEquals(sysUser.getEmail(), dto.getEmail())) {
                        throw new BusinessException(BusinessMsgState.EMAIL_EXISTING);
                    }
                    sysUser.setEmail(dto.getEmail());
                    super.updateEntityById(sysUser);
                }
            } catch (BusinessException e) {
                businessException = e;
            } catch (Exception e) {
                throw new BusinessException(BusinessMsgState.OPT_ERROR);
            }finally {
                validateService.clear(dto.getEmail());
            }
        }
        if (!ObjectUtils.isEmpty(businessException)) {
            throw  businessException;
        }
        return Optional.empty();
    }

    /**
     * 个人中心信息修改
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateInfo(UpdateInfoDTO dto) {
        // 查询当前用户是否存在
        SysUser sysUser = super.lambdaQuery()
                               .eq(SysUser::getId, dto.getId())
                               .oneOpt()
                               .orElseThrow(() -> new BusinessException(BusinessMsgState.DATA_NOT_FOUNT));
        // 不允许非手机号
        if (!ValidateUtil.isPhone(dto.getPhone())){
            throw new BusinessException(BusinessMsgState.NOT_PHONE);
        }

        // 校验手机号是否变化
        if (dto.getPhone() != null && !ObjectUtils.nullSafeEquals(dto.getPhone(), sysUser.getPhone())) {
            // 手机号已存在
            super.lambdaQuery()
                 .eq(SysUser::getPhone, dto.getPhone())
                 .oneOpt()
                 .ifPresent(var -> {
                     throw new BusinessException(BusinessMsgState.PHONE_EXISTING);
                 });
            sysUser.setPhone(dto.getPhone());
        }
        sysUser.setGender(dto.getGender());
        sysUser.setNickName(dto.getNickName());
        super.updateEntityById(sysUser);
        return Optional.empty();
    }

    /**
     * 新增用户
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> saveUser(UserDTO dto) {
        this.validateUsername(dto.getUsername());
        this.validatePhone(dto.getPhone());
        super.lambdaQuery()
             .select(SysUser::getUsername,SysUser::getEmail, SysUser::getPhone)
             .and(sysUserLambdaQueryWrapper -> {
                 // 校验用户名是否唯一
                 // 校验邮箱是否唯一
                 // 校验手机号是否唯一
                 sysUserLambdaQueryWrapper.eq(SysUser::getUsername, dto.getUsername())
                                          .or()
                                          .eq(SysUser::getEmail, dto.getEmail())
                                          .or()
                                          .eq(SysUser::getPhone, dto.getPhone());
             })
             .list()
             .forEach(sysUser -> {
                super.validateFields(sysUser, dto, collection -> {
                    if (!CollectionUtils.isEmpty(collection)) {
                        throw new UserException(UserState.USER_INFO_EXISTING, collection.toString());
                    }
                });
             });
        SysUser sysUser = this.convert.toEntity(dto);
        sysUser.setPassword(DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD_BYTE));
        super.savaEntity(sysUser);
        super.baseMapper.savePositionRelationById(sysUser.getId(), dto.getPositionIds());
        super.baseMapper.savaRoleRelationById(sysUser.getId(), dto.getRoleIds());
        return Optional.empty();
    }

    /**
     * 更新用户
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateUser(UserDTO dto) {
        this.validatePhone(dto.getPhone());
        // 查询用户是否存在
        SysUser sysUser = super.lambdaQuery()
                                .eq(SysUser::getId, dto.getId())
                                .oneOpt().orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
        long sessionId = SessionManagement.getSessionId();
        if (!dto.getEnabled()) {
            // 禁止禁用管理员
            if (SessionManagement.isOwner(sysUser.getUsername())) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
            // 禁止禁用当前操作用户
            if (ObjectUtils.nullSafeEquals(sessionId, sysUser.getId())) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
        }
        // 验证用户名，邮箱，手机是否已存在
        super.lambdaQuery()
             .select(SysUser::getUsername, SysUser::getEmail, SysUser::getPhone)
             .ne(SysUser::getId, dto.getId())
             .and(sysUserLambdaQueryWrapper -> {
                 sysUserLambdaQueryWrapper.eq(SysUser::getUsername, dto.getUsername())
                                          .or()
                                          .eq(SysUser::getEmail, dto.getEmail())
                                          .or()
                                          .eq(SysUser::getPhone, dto.getPhone());
             })
             .list()
             .forEach(entity -> {
                 super.validateFields(entity, dto, collection -> {
                     if (!CollectionUtils.isEmpty(collection)) {
                         throw new UserException(UserState.USER_INFO_EXISTING, collection.toString());
                     }
                 });
             });
        this.convert.copyProperties(dto, sysUser);
        super.updateEntityById(sysUser);
        this.updateDependents(dto);
        return Optional.empty();
    }

    /**
     * 更新用户依赖关系
     *
     * @param dto dto
     */
    private void updateDependents(UserDTO dto) {
        Set<Long> relationId = Collections.singleton(dto.getId());
        Optional.ofNullable(dto.getPositionIds())
                .ifPresent(jobIds -> {
                    if (!CollectionUtils.isEmpty(jobIds)) {
                        super.baseMapper.deletePositionRelationByIds(relationId);
                        super.baseMapper.savePositionRelationById(dto.getId(), dto.getPositionIds());
                    }
                });
        Optional.ofNullable(dto.getRoleIds())
                .ifPresent(roleIds -> {
                    if (!CollectionUtils.isEmpty(roleIds)) {
                        super.baseMapper.deleteRoleRelationByIds(relationId);
                        super.baseMapper.savaRoleRelationById(dto.getId(), dto.getRoleIds());
                    }
                });
    }

    /**
     * 验证手机号
     *
     * @param phone phone
     */
    private void validatePhone(String phone) {
        if (ValidateUtil.isNotPhone(phone)) {
            throw new BusinessException(BusinessMsgState.NOT_PHONE);
        }
    }

    private void validateUsername(String username) {
        if (!ValidateUtil.isUserName(username)) {
            throw new BusinessException(BusinessMsgState.USERNAME_LOW);
        }
    }
    /**
     * 删除用户
     *
     * @param ids ids
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deleteUser(Set<Long> ids) {
        if (SessionManagement.isOwner()) {
            SysUser sysUser = super.lambdaQuery()
                                   .eq(SysUser::getUsername, Objects.requireNonNull(SessionManagement.getSession())
                                                                    .getUsername())
                                   .oneOpt().orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
            if (ids.contains(sysUser.getId())) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
        }
        super.deleteByIds(ids);
        super.baseMapper.deleteRoleRelationByIds(ids);
        this.sysPositionDao.deleteUserRelationById(ids);
        long sessionId = SessionManagement.getSessionId();
        // 相当于注销
        if (ids.contains(sessionId)) {
            SessionManagement.removeSession(sessionId);
        }
        return Optional.empty();
    }
}
