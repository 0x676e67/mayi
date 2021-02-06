package com.zf1976.ant.upms.biz.convert;


import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.ant.upms.biz.convert.base.Convert;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author Windows
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysUserConvert extends Convert<SysUser, UserVO, UserDTO> {
    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);
}
