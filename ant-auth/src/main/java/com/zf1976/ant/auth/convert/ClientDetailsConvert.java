package com.zf1976.ant.auth.convert;

import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.ClientDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/4/26
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientDetailsConvert {
    ClientDetailsConvert INSTANCE = Mappers.getMapper(ClientDetailsConvert.class);

    /**
     * 客户端dto转实体类
     *
     * @param clientDetailsDTO dto
     * @return /
     */
    ClientDetails toEntity(ClientDetailsDTO clientDetailsDTO);



}
