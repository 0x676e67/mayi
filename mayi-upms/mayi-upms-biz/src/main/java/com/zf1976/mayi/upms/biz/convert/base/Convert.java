package com.zf1976.mayi.upms.biz.convert.base;

import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * @author mac
 */
public interface Convert<E,V,D> {

    /**
     * DTO转Entity
     *
     * @param dto 入参
     * @return entity
     */
    E toEntity(D dto);

    /**
     * DTO集合转Entity集合
     *
     * @param dtoList 入参列表
     * @return entity list
     */
    List<E> toEntity(List<D> dtoList);

    /**
     * Entity转VO
     *
     * @param entity entity
     * @return vo
     */
    V toVo(E entity);

    /**
     * Entity集合转VO集合
     *
     * @param entityList entityList
     * @return vo list
     */
    List <V> toVo(List<E> entityList);

    /**
     * 复制属性
     *
     * @param source source
     * @param target target
     */
    void copyProperties(D source, @MappingTarget E target);

}
