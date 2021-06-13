package com.zf1976.mayi.upms.biz.convert;


import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.convert.base.Convert;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysUser;
import com.zf1976.mayi.upms.biz.pojo.vo.user.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author Windows
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysUserConvert extends Convert<SysUser, UserVO, UserDTO> {
    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * 复制属性
     *
     * @param source source
     * @param target target
     */
    @Mapping(target = "roleList", ignore = true)
    @Mapping(target = "positionList", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "dataPermissions", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "avatarPath", ignore = true)
    @Mapping(target = "avatarName", ignore = true)
    void copyProperties(UserDTO source, @MappingTarget User target);

    /**
     * 复制属性
     *
     * @param dto source
     * @param user target
     */
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roleList", ignore = true)
    @Mapping(target = "positionList", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "dataPermissions", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "avatarPath", ignore = true)
    @Mapping(target = "avatarName", ignore = true)
    void copyProperties(UpdateInfoDTO dto, @MappingTarget User user);


    /**
     * 转 认证信息
     *
     * @param sysUser 系统用户
     * @return /
     */
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "dataPermissions", ignore = true)
    @Mapping(target = "avatarPath", ignore = true)
    User convert(SysUser sysUser);
}
