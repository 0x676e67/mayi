package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.convert.base.Convert;
import com.zf1976.ant.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import com.zf1976.ant.upms.biz.pojo.vo.role.RoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2020/11/21
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysRoleConvert extends Convert<SysRole, RoleVO, RoleDTO> {
    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);
}
