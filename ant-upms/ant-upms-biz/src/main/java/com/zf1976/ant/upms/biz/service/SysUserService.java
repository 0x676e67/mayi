package com.zf1976.ant.upms.biz.service;




import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.power.common.util.ValidateUtil;
import com.zf1976.ant.common.component.load.annotation.CaffeineEvict;
import com.zf1976.ant.common.component.load.annotation.CaffeinePut;
import com.zf1976.ant.common.component.load.annotation.Space;
import com.zf1976.ant.common.component.mail.ValidateFactory;
import com.zf1976.ant.common.component.mail.ValidateService;
import com.zf1976.ant.upms.biz.config.FileProperties;
import com.zf1976.ant.common.encrypt.RsaUtil;
import com.zf1976.ant.common.encrypt.config.RsaProperties;
import com.zf1976.ant.common.security.cache.session.Session;
import com.zf1976.ant.common.security.safe.SecurityUserDetails;
import com.zf1976.ant.common.security.safe.SecurityContextHolder;
import com.zf1976.ant.common.security.safe.filter.manager.SessionContextHolder;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.common.core.foundation.exception.BadBusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.upms.biz.convert.SysUserConvert;
import com.zf1976.ant.upms.biz.dao.SysDepartmentDao;
import com.zf1976.ant.upms.biz.dao.SysPositionDao;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
import com.zf1976.ant.upms.biz.exception.enums.UserState;
import com.zf1976.ant.upms.biz.exception.UserException;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateEmailDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdatePasswordDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import com.zf1976.ant.upms.biz.pojo.query.UserQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 系统用户(SysUser)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:46:01
 */
@Slf4j
@Service
public class SysUserService extends AbstractService<SysUserDao, SysUser> {

    private final AlternativeJdkIdGenerator jdkIdGenerator;
    private final PasswordEncoder passwordEncoder;
    private final SysDepartmentDao sysDepartmentDao;
    private final SysPositionDao sysPositionDao;
    private final SysRoleDao sysRoleDao;
    private final SysUserConvert convert;

    public SysUserService(PasswordEncoder passwordEncoder,
                          SysDepartmentDao sysDepartmentDao,
                          SysPositionDao sysJobDao,
                          SysRoleDao sysRoleDao) {
        this.jdkIdGenerator = new AlternativeJdkIdGenerator();
        this.passwordEncoder = passwordEncoder;
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysPositionDao = sysJobDao;
        this.sysRoleDao = sysRoleDao;
        this.convert = SysUserConvert.INSTANCE;
    }

    /**
     * 按条件分页查询用户
     *
     * @param requestPage request page
     * @return /
     */
    @CaffeinePut(namespace = Space.USER, key = "#requestPage", dynamicsKey = true)
    public IPage<UserVO> selectUserPage(RequestPage<UserQueryParam> requestPage) {
        Assert.notNull(requestPage, "request page can not been null");
        IPage<SysUser> sourcePage;
        // 非super admin 过滤数据权限
        if (!SecurityContextHolder.isSuperAdmin()) {
            SecurityUserDetails userDetails = (SecurityUserDetails) SecurityContextHolder.getDetails();
            Assert.notNull(userDetails, "user details can not been null");
            // 用户可观察数据范围
            List<Long> userIds = super.baseMapper.selectByDepartmentIds(userDetails.getDataScopes());
            sourcePage = super.queryChain()
                             .setQueryParam(requestPage, () -> {
                                 // 自定义条件
                                 return ChainWrappers.queryChain(super.baseMapper)
                                                     .in(getColumn(SysUser::getId), userIds);
                             }).selectPage();
        } else {
            sourcePage = super.queryChain()
                              .setQueryParam(requestPage)
                              .selectPage();
        }
        // 根据部门分页
        Optional.ofNullable(requestPage.getQuery())
                .ifPresent(userQueryParam -> {
                    if (userQueryParam.getDepartmentId() != null) {
                        Long departmentId = userQueryParam.getDepartmentId();
                        Set<Long> ids = new HashSet<>();
                        ids.add(departmentId);
                        this.sysDepartmentDao.selectChildrenById(departmentId)
                                             .stream()
                                             .map(SysDepartment::getId)
                                             .forEach(id -> {
                                                 this.getDepartmentTreeIds(id, () -> ids);
                                             });
                        List<SysUser> collect = sourcePage.getRecords()
                                                          .stream()
                                                          .filter(sysUser -> ids.contains(sysUser.getDepartmentId()))
                                                          .collect(Collectors.toList());
                        sourcePage.setRecords(collect);
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
    public void getDepartmentTreeIds(Long departmentId, Supplier<Collection<Long>> supplier){
        Assert.notNull(departmentId, "department id can not been null");
        supplier.get().add(departmentId);
        this.sysDepartmentDao.selectChildrenById(departmentId)
                             .stream()
                             .map(SysDepartment::getId)
                             .forEach(id -> {
                                 // 继续下一级子节点
                                 this.getDepartmentTreeIds(id, supplier);
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
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateAvatar(MultipartFile multipartFile) {
        final Long id = SecurityContextHolder.getPrincipalId();
        final SysUser sysUser = super.lambdaQuery()
                                 .eq(SysUser::getId, id)
                                 .one();
        try {
            final String oldFileName = sysUser.getAvatarName();
            final StringBuilder oldName = new StringBuilder(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            final StringBuilder freshName = oldName.insert(oldName.lastIndexOf("."), "-" + jdkIdGenerator.generateId());
            final Path path = Paths.get(FileProperties.getAvatarRealPath(), String.valueOf(freshName));
            multipartFile.transferTo(path);
            Files.deleteIfExists(Paths.get(FileProperties.getAvatarRealPath(), oldFileName));
            sysUser.setAvatarName(String.valueOf(freshName));
            super.updateEntityById(sysUser);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new BadBusinessException(BusinessMsgState.UPLOAD_ERROR);
        }
        return Optional.empty();
    }

    /**
     * 修改密码
     *
     * @param dto dto
     * @return /
     */
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updatePassword(UpdatePasswordDTO dto) {
        final Long id = SecurityContextHolder.getPrincipalId();
        final SysUser sysUser = super.lambdaQuery()
                                     .eq(SysUser::getId, id)
                                     .oneOpt().orElseThrow(() -> new BadBusinessException(BusinessMsgState.CODE_NOT_FOUNT));
        String oldPass;
        String newPass;
        try {
            oldPass = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getOldPass());
            newPass = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getNewPass());
        } catch (Exception e) {
            throw new BadBusinessException(BusinessMsgState.OPT_ERROR);
        }

        if (StringUtils.isEmpty(oldPass) || StringUtils.isEmpty(newPass)){
            throw new BadBusinessException(BusinessMsgState.NULL_PASSWORD);
        }

        if (oldPass.equals(newPass)) {
            throw new BadBusinessException(BusinessMsgState.PASSWORD_REPEAT);
        }

        if (passwordEncoder.matches(oldPass, sysUser.getPassword()) && !ValidateUtil.isPassword(newPass)) {
            sysUser.setPassword(passwordEncoder.encode(newPass));
            super.updateEntityById(sysUser);
            // 强制用户重新登陆
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            SessionContextHolder.removeSession(id);
        }else {
            throw new BadBusinessException(BusinessMsgState.PASSWORD_LOW);
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
    @CaffeineEvict(namespace =  Space.USER)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateEmail(String code, UpdateEmailDTO dto) {

        if (StringUtils.isEmpty(code) || ObjectUtils.isEmpty(dto)) {
            throw new BadBusinessException(BusinessMsgState.PARAM_ILLEGAL);
        }

        ValidateService validateService = ValidateFactory.getInstance();
        UserDetails userDetails = SecurityContextHolder.getDetails();
        BadBusinessException businessException = null;
        if (validateService.validate(dto.getEmail(), code)) {
            try {
                String decodePassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getPassword());
                if (passwordEncoder.matches(decodePassword, userDetails.getPassword())) {
                    Long principalId = SecurityContextHolder.getPrincipalId();
                    SysUser sysUser = super.lambdaQuery()
                                           .eq(SysUser::getId, principalId)
                                           .oneOpt().orElseThrow(() -> new BadBusinessException(BusinessMsgState.DATA_NOT_FOUNT));
                    if (ObjectUtils.nullSafeEquals(sysUser.getEmail(), dto.getEmail())) {
                        throw new BadBusinessException(BusinessMsgState.EMAIL_EXISTING);
                    }
                    sysUser.setEmail(dto.getEmail());
                    super.updateEntityById(sysUser);
                }
            } catch (BadBusinessException e) {
                businessException = e;
            } catch (Exception e) {
                throw new BadBusinessException(BusinessMsgState.OPT_ERROR);
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
    @CaffeineEvict(namespace = Space.USER)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateInfo(UpdateInfoDTO dto) {
        SysUser sysUser = super.lambdaQuery()
                               .eq(SysUser::getId, dto.getId())
                               .oneOpt().orElseThrow(() -> new BadBusinessException(BusinessMsgState.DATA_NOT_FOUNT));
        Long id = SecurityContextHolder.getPrincipalId();
        Session session = SessionContextHolder.get(id);
        Assert.notNull(session, "session cannot been null");
        if (!ObjectUtils.isEmpty(dto.getPhone()) && !ObjectUtils.nullSafeEquals(dto.getPhone(), sysUser.getPhone())) {
            SysUser var = super.lambdaQuery()
                               .eq(SysUser::getPhone, dto.getPhone())
                               .one();
            if (!ObjectUtils.isEmpty(var)) {
                throw new BadBusinessException(BusinessMsgState.PHONE_EXISTING);
            }else if (ValidateUtil.isPhone(dto.getPhone())){
                throw new BadBusinessException(BusinessMsgState.NOT_PHONE);
            }
            sysUser.setPhone(dto.getPhone());
        }
        if (!ObjectUtils.isEmpty(session) && !ObjectUtils.isEmpty(dto.getNickName())) {
            session.setNickName(dto.getNickName());
        }
        sysUser.setGender(dto.getGender());
        sysUser.setNickName(dto.getNickName());
        super.updateEntityById(sysUser);
        SessionContextHolder.refresh(id, session);
        return Optional.empty();
    }

    /**
     * 新增用户
     *
     * @param dto dto
     * @return /
     */
    @CaffeineEvict(namespace =  Space.USER)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> saveUser(UserDTO dto) {
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
                        throw new UserException(UserState.USER_EXISTING, collection.toString());
                    }
                });
             });
        SysUser sysUser = this.convert.toEntity(dto);
        String principal = SecurityContextHolder.getPrincipal();
        sysUser.setCreateBy(principal);
        sysUser.setCreateTime(new Date());
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
    @CaffeineEvict(namespace =  Space.USER)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateUser(UserDTO dto) {
        this.validatePhone(dto.getPhone());
        // 查询用户是否存在
        SysUser sysUser = super.lambdaQuery()
                                .eq(SysUser::getId, dto.getId())
                                .oneOpt().orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
        long principalId = SecurityContextHolder.getPrincipalId();
        if (!dto.getEnabled()) {
            // 禁止禁用管理员
            if (SecurityContextHolder.validateSuperAdmin(sysUser.getUsername())) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
            // 禁止禁用当前操作用户
            if (ObjectUtils.nullSafeEquals(principalId, sysUser.getId())) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
            SessionContextHolder.removeSession(sysUser.getId());
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
        Set<Long> singletonId = Collections.singleton(dto.getId());
        Optional.ofNullable(dto.getPositionIds())
                .ifPresent(jobIds -> {
                    if (!CollectionUtils.isEmpty(jobIds)) {
                        super.baseMapper.deletePositionRelationByIds(singletonId);
                        super.baseMapper.savePositionRelationById(dto.getId(), dto.getPositionIds());
                    }
                });
        Optional.ofNullable(dto.getRoleIds())
                .ifPresent(roleIds -> {
                    if (!CollectionUtils.isEmpty(roleIds)) {
                        super.baseMapper.deleteRoleRelationByIds(singletonId);
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
            throw new BadBusinessException(BusinessMsgState.NOT_PHONE);
        }
    }
    /**
     * 删除用户
     *
     * @param ids ids
     * @return /
     */
    @CaffeineEvict(namespace =  Space.USER)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deleteUser(Set<Long> ids) {
        if (SecurityContextHolder.isSuperAdmin()) {
            SysUser sysUser = super.lambdaQuery()
                                   .eq(SysUser::getUsername, SecurityContextHolder.getPrincipal())
                                   .oneOpt().orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
            if (ids.contains(sysUser.getId())) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
        }
        super.deleteByIds(ids);
        super.baseMapper.deleteRoleRelationByIds(ids);
        this.sysPositionDao.deleteUserRelationById(ids);
        long principalId = SecurityContextHolder.getPrincipalId();
        // 相当于注销
        if (ids.contains(principalId)) {
            SecurityContextHolder.clearContext();
            SessionContextHolder.removeSession(principalId);
        }
        return Optional.empty();
    }
}
