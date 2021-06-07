package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.upms.biz.convert.base.Convert;
import com.zf1976.mayi.upms.biz.pojo.dto.dict.DictDetailDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysDictDetail;
import com.zf1976.mayi.upms.biz.pojo.vo.dict.DictDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author ant
 * Create by Ant on 2020/10/24 4:24 下午
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysDictDetailConvert extends Convert<SysDictDetail, DictDetailVO, DictDetailDTO> {
    SysDictDetailConvert INSTANCE = Mappers.getMapper(SysDictDetailConvert.class);
}
