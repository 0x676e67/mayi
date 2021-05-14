package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.auth.convert.SecurityConvert;
import com.zf1976.ant.auth.dao.SysPermissionDao;
import com.zf1976.ant.auth.exception.SecurityException;
import com.zf1976.ant.auth.pojo.dto.PermissionDTO;
import com.zf1976.ant.auth.pojo.po.SysPermission;
import com.zf1976.ant.auth.pojo.vo.PermissionVO;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/5/11
 */
@Service
public class PermissionService extends ServiceImpl<SysPermissionDao, SysPermission> {

    private final SecurityConvert convert = SecurityConvert.INSTANCE;

    /**
     * 根据分页对象分页查询权限列表
     *
     * @date 2021-05-12 00:18:24
     * @param page 分页对象
     * @return {@link IPage< PermissionVO>}
     */
    public IPage<PermissionVO> selectPermissionByPage(Page<SysPermission> page) {
        Page<SysPermission> permissionPage = super.lambdaQuery()
                                         .select(SysPermission::getId,
                                                 SysPermission::getName,
                                                 SysPermission::getValue,
                                                 SysPermission::getDescription,
                                                 SysPermission::getCreateBy)
                                         .page(page);
        return this.mapToTarget(permissionPage, convert::toPermissionVo);
    }

    /**
     * 分页对象类型转换
     *
     * @date 2021-05-12 09:37:44
     * @param sourcePage 源分页对象
     * @param translator 翻译
     * @return {@link IPage<PermissionVO>}
     */
    private IPage<PermissionVO> mapToTarget(IPage<SysPermission> sourcePage, Function<SysPermission, PermissionVO> translator) {
        List<PermissionVO> targetPageList = sourcePage.getRecords()
                                               .stream()
                                               .map(translator)
                                               .collect(Collectors.toList());
        return new Page<PermissionVO>(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount()).setRecords(targetPageList);
    }

    /**
     * 新增权限数据
     *
     * @date 2021-05-12 09:37:12
     * @param permissionDTO DTO
     * @return {@link Void}
     */
    @Transactional(rollbackFor = Exception.class)
    public Void savePermission(PermissionDTO permissionDTO) {
        // 判断权限值是否已经存在
        if (this.checkExitsPermissionValue(permissionDTO.getValue())) {
            throw new SecurityException("permission value：" + permissionDTO.getValue() + "is exist");
        }
        SysPermission sysPermission = this.convert.toPermissionEntity(permissionDTO);
        if (!super.saveOrUpdate(sysPermission)) {
            throw new SecurityException("Permission data insert failed");
        }
        return null;
    }

    /**
     * 更新权限数据
     *
     * @date 2021-05-12 09:36:52
     * @param permissionDTO DTO
     * @return {@link Void}
     */
    @Transactional(rollbackFor = Exception.class)
    public Void updatePermission(PermissionDTO permissionDTO) {
        // 判断权限该权限实体是否存在
        SysPermission sysPermission = super.lambdaQuery()
                                           .eq(SysPermission::getId, permissionDTO.getId())
                                           .oneOpt()
                                           .orElseThrow(() -> new SecurityException("permission does not exist"));
        // 如果更新的权限值改变
        if (!ObjectUtils.nullSafeEquals(sysPermission.getValue(), permissionDTO.getValue())) {
            // 判断权限值是否已经存在
            if (this.checkExitsPermissionValue(permissionDTO.getValue())) {
                throw new SecurityException("permission value：" + permissionDTO.getValue() + "is exist");
            }
        }
        this.convert.copyProperties(permissionDTO, sysPermission);
        if (!super.saveOrUpdate(sysPermission)) {
            throw new SecurityException("Permission data update failed");
        }
        return null;
    }

    /**
     * 检查权限值是否已存在
     *
     * @date 2021-05-12 09:36:16
     * @param value 泉限值
     * @return {@link boolean}
     */
    private boolean checkExitsPermissionValue(String value) {
        return super.lambdaQuery()
                    .eq(SysPermission::getValue, value)
                    .oneOpt()
                    .isPresent();
    }



    /**
     * 根据权限属性id删除
     *
     * @date 2021-05-12 00:46:53
     * @param id 权限id
     * @return {@link Void}
     */
    @Transactional(rollbackFor = Exception.class)
    public Void deletePermissionById(Long id) {
        if (id != null) {
            super.removeById(id);
            return null;
        }
        throw new SecurityException("permission id cannot been null");
    }

    /**
     * 根据id集合删除
     *
     * @date 2021-05-12 20:26:06
     * @param ids id集合
     * @return {@link Void}
     */
    @Transactional(rollbackFor = Exception.class)
    public Void deletePermissionByIds(Set<Long> ids) {
        //
        if (!super.removeByIds(ids)) {
            throw new SecurityException("permission ids is empty");
        }
        return null;
    }
}
