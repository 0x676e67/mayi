package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDict;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictVO;
import com.zf1976.ant.upms.biz.convert.base.Convert;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2020/10/23 3:04 下午
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysDictConvert extends Convert<SysDict, DictVO, DictDTO> {
    SysDictConvert INSTANCE = Mappers.getMapper(SysDictConvert.class);
}
