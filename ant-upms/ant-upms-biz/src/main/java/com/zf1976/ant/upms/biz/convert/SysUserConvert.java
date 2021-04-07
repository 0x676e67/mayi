package com.zf1976.ant.upms.biz.convert;


import com.zf1976.ant.common.security.pojo.UserInfo;
import com.zf1976.ant.upms.biz.convert.base.Convert;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import org.mapstruct.Mapper;
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
    void copyProperties(UserDTO source, @MappingTarget UserInfo target);

    /**
     * 复制属性
     *
     * @param dto source
     * @param userInfo target
     */
    void copyProperties(UpdateInfoDTO dto,@MappingTarget UserInfo userInfo);
}
