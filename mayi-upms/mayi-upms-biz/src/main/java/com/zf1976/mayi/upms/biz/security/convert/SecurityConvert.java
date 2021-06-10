package com.zf1976.mayi.upms.biz.security.convert;

import com.zf1976.mayi.upms.biz.pojo.dto.PermissionDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysPermission;
import com.zf1976.mayi.upms.biz.pojo.vo.PermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/5/11
 */
@SuppressWarnings("all")
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SecurityConvert {

    SecurityConvert INSTANCE = Mappers.getMapper(SecurityConvert.class);

    /**
     * 转权限vo
     *
     * @param sysPermission 系统权限实体对象
     * @return {@link PermissionVO}
     * @date 2021-05-11 23:56:07
     */
    PermissionVO toPermissionVO(SysPermission sysPermission);

    /**
     * permission DTO 转实体
     * @param permissionDTO DTO
     * @return {@link SysPermission}
     */
    SysPermission toPermissionEntity(PermissionDTO permissionDTO);

    /**
     * 复制属性
     *
     * @param permissionDTO DTO
     * @param sysPermission 实体
     * @throws
     * @date 2021-05-12 09:13:09
     */
    void copyProperties(PermissionDTO permissionDTO, @MappingTarget SysPermission sysPermission);


}
