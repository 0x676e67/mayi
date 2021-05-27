package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDict;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-05-27T16:40:26+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class SysDictConvertImpl implements SysDictConvert {

    @Override
    public SysDict toEntity(DictDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysDict sysDict = new SysDict();

        sysDict.setId( dto.getId() );
        sysDict.setDictName( dto.getDictName() );
        sysDict.setDescription( dto.getDescription() );

        return sysDict;
    }

    @Override
    public List<SysDict> toEntity(List<DictDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysDict> list = new ArrayList<SysDict>( dtoList.size() );
        for ( DictDTO dictDTO : dtoList ) {
            list.add( toEntity( dictDTO ) );
        }

        return list;
    }

    @Override
    public DictVO toVo(SysDict entity) {
        if ( entity == null ) {
            return null;
        }

        DictVO dictVO = new DictVO();

        dictVO.setId( entity.getId() );
        dictVO.setDictName( entity.getDictName() );
        dictVO.setDescription( entity.getDescription() );

        return dictVO;
    }

    @Override
    public List<DictVO> toVo(List<SysDict> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<DictVO> list = new ArrayList<DictVO>( entityList.size() );
        for ( SysDict sysDict : entityList ) {
            list.add( toVo( sysDict ) );
        }

        return list;
    }

    @Override
    public void copyProperties(DictDTO source, SysDict target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        target.setDictName( source.getDictName() );
        target.setDescription( source.getDescription() );
    }
}
