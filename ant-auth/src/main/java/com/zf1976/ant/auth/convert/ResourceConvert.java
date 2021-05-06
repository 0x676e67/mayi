package com.zf1976.ant.auth.convert;

import com.zf1976.ant.auth.pojo.ResourceTree;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/5/6
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceConvert {
    ResourceConvert INSTANCE = Mappers.getMapper(ResourceConvert.class);

}
