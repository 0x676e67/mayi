package com.zf1976.mayi.upms.biz.security.convert;

import com.zf1976.mayi.upms.biz.pojo.po.ClientDetails;
import com.zf1976.mayi.upms.biz.pojo.dto.ClientDetailsDTO;
import com.zf1976.mayi.upms.biz.pojo.vo.ClientDetailsVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/6/10
 */
@SuppressWarnings("all")
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientConvert {

    ClientConvert INSTANCE = Mappers.getMapper(ClientConvert.class);


    /**
     * 实体转vo
     *
     * @param clientDetails 实体
     * @return {@link ClientDetailsVO}
     */
    ClientDetailsVO toClientDetailsVO(ClientDetails clientDetails);


    /**
     * 客户端dto转实体类
     *
     * @param clientDetailsDTO DTO
     * @return {@link ClientDetails}
     * @date 2021-05-11 23:56:50
     */
    ClientDetails toClientDetailsEntity(ClientDetailsDTO clientDetailsDTO);


    /**
     * 复制属性
     *
     * @param clientDetailsDTO DTO
     * @param clientDetails    实体
     * @throws
     */
    void copyProperties(ClientDetailsDTO clientDetailsDTO, @MappingTarget ClientDetails clientDetails);
}
