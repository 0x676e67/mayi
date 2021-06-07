package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysPosition;
import com.zf1976.mayi.upms.biz.pojo.vo.job.PositionVO;
import com.zf1976.mayi.upms.biz.convert.base.Convert;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2020/10/25 5:37 下午
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysPositionConvert extends Convert<SysPosition, PositionVO, PositionDTO> {
    SysPositionConvert INSTANCE = Mappers.getMapper(SysPositionConvert.class);
}
