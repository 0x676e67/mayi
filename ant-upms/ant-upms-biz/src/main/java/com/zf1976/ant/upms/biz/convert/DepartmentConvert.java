package com.zf1976.ant.upms.biz.convert;


import com.zf1976.ant.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.ant.upms.biz.convert.base.Convert;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2020/10/26 7:26 下午
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentConvert extends Convert<SysDepartment, DepartmentVO, DepartmentDTO> {
    DepartmentConvert INSTANCE = Mappers.getMapper(DepartmentConvert.class);
}
