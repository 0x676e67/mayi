package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDetailDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDictDetail;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictDetailVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-05-26T23:23:18+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class SysDictDetailConvertImpl implements SysDictDetailConvert {

    @Override
    public SysDictDetail toEntity(DictDetailDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysDictDetail sysDictDetail = new SysDictDetail();

        sysDictDetail.setId( dto.getId() );
        sysDictDetail.setDictId( dto.getDictId() );
        sysDictDetail.setLabel( dto.getLabel() );
        sysDictDetail.setValue( dto.getValue() );
        sysDictDetail.setDictSort( dto.getDictSort() );

        return sysDictDetail;
    }

    @Override
    public List<SysDictDetail> toEntity(List<DictDetailDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysDictDetail> list = new ArrayList<SysDictDetail>( dtoList.size() );
        for ( DictDetailDTO dictDetailDTO : dtoList ) {
            list.add( toEntity( dictDetailDTO ) );
        }

        return list;
    }

    @Override
    public DictDetailVO toVo(SysDictDetail entity) {
        if ( entity == null ) {
            return null;
        }

        DictDetailVO dictDetailVO = new DictDetailVO();

        dictDetailVO.setId( entity.getId() );
        dictDetailVO.setDictId( entity.getDictId() );
        dictDetailVO.setLabel( entity.getLabel() );
        dictDetailVO.setValue( entity.getValue() );
        dictDetailVO.setDictSort( entity.getDictSort() );

        return dictDetailVO;
    }

    @Override
    public List<DictDetailVO> toVo(List<SysDictDetail> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<DictDetailVO> list = new ArrayList<DictDetailVO>( entityList.size() );
        for ( SysDictDetail sysDictDetail : entityList ) {
            list.add( toVo( sysDictDetail ) );
        }

        return list;
    }

    @Override
    public void copyProperties(DictDetailDTO source, SysDictDetail target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        target.setDictId( source.getDictId() );
        target.setLabel( source.getLabel() );
        target.setValue( source.getValue() );
        target.setDictSort( source.getDictSort() );
    }
}
