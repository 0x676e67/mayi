package com.zf1976.ant.auth.convert;

import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.ClientDetailsDTO;
import com.zf1976.ant.auth.pojo.dto.PermissionDTO;
import com.zf1976.ant.auth.pojo.po.SysPermission;
import com.zf1976.ant.auth.pojo.vo.PermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/5/11
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
@SuppressWarnings("all")
public interface SecurityConvert {

    SecurityConvert INSTANCE = Mappers.getMapper(SecurityConvert.class);

    /**
     * 转权限vo
     *
     * @date 2021-05-11 23:56:07
     * @param sysPermission 系统权限实体对象
     * @return {@link PermissionVO}
     */
    PermissionVO toPermissionVo(SysPermission sysPermission);


    /**
     * 客户端dto转实体类
     *
     * @date 2021-05-11 23:56:50
     * @param clientDetailsDTO DTO
     * @return {@link ClientDetails}
     */
    ClientDetails toClientDetailsEntity(ClientDetailsDTO clientDetailsDTO);

    /**
     * permission DTO 转实体
     * @param permissionDTO DTO
     * @return {@link SysPermission}
     */
    SysPermission toPermissionEntity(PermissionDTO permissionDTO);

    /**
     * 复制属性
     *
     * @date 2021-05-12 09:13:09
     * @param permissionDTO 权限DTO
     * @param sysPermission 权限实体
     * @throws
     */
    void copyProperties(PermissionDTO permissionDTO, @MappingTarget SysPermission sysPermission);

}
