package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.common.security.support.session.Session;
import com.zf1976.mayi.upms.biz.pojo.vo.SessionVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/1/23
 **/

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionConvert {
    SessionConvert INSTANCE = Mappers.getMapper(SessionConvert.class);

    /**
     * to vo
     *
     * @param session session
     * @return vo
     */
    SessionVO toVO(Session session);
}
