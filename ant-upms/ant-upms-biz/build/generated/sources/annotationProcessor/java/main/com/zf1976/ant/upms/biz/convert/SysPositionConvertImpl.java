package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import com.zf1976.ant.upms.biz.pojo.vo.job.PositionVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T19:16:39+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
public class SysPositionConvertImpl implements SysPositionConvert {

    @Override
    public SysPosition toEntity(PositionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysPosition sysPosition = new SysPosition();

        sysPosition.setId( dto.getId() );
        sysPosition.setName( dto.getName() );
        sysPosition.setEnabled( dto.getEnabled() );
        sysPosition.setJobSort( dto.getJobSort() );

        return sysPosition;
    }

    @Override
    public List<SysPosition> toEntity(List<PositionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysPosition> list = new ArrayList<SysPosition>( dtoList.size() );
        for ( PositionDTO positionDTO : dtoList ) {
            list.add( toEntity( positionDTO ) );
        }

        return list;
    }

    @Override
    public PositionVO toVo(SysPosition entity) {
        if ( entity == null ) {
            return null;
        }

        PositionVO positionVO = new PositionVO();

        positionVO.setId( entity.getId() );
        positionVO.setName( entity.getName() );
        positionVO.setEnabled( entity.getEnabled() );
        positionVO.setJobSort( entity.getJobSort() );
        positionVO.setCreateTime( entity.getCreateTime() );

        return positionVO;
    }

    @Override
    public List<PositionVO> toVo(List<SysPosition> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PositionVO> list = new ArrayList<PositionVO>( entityList.size() );
        for ( SysPosition sysPosition : entityList ) {
            list.add( toVo( sysPosition ) );
        }

        return list;
    }

    @Override
    public void copyProperties(PositionDTO source, SysPosition target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        target.setName( source.getName() );
        target.setEnabled( source.getEnabled() );
        target.setJobSort( source.getJobSort() );
    }

    @Override
    public PositionVO clone(PositionVO source, PositionVO target) {
        if ( source == null ) {
            return null;
        }

        target.setId( source.getId() );
        target.setName( source.getName() );
        target.setEnabled( source.getEnabled() );
        target.setJobSort( source.getJobSort() );
        target.setCreateTime( source.getCreateTime() );

        return target;
    }
}
