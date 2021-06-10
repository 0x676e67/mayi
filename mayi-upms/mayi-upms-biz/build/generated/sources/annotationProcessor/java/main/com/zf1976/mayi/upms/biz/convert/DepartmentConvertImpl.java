package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import com.zf1976.mayi.upms.biz.pojo.vo.dept.DepartmentVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-10T14:08:13+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class DepartmentConvertImpl implements DepartmentConvert {

    @Override
    public SysDepartment toEntity(DepartmentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysDepartment sysDepartment = new SysDepartment();

        sysDepartment.setId( dto.getId() );
        sysDepartment.setPid( dto.getPid() );
        sysDepartment.setName( dto.getName() );
        sysDepartment.setDeptSort( dto.getDeptSort() );
        sysDepartment.setEnabled( dto.getEnabled() );

        return sysDepartment;
    }

    @Override
    public List<SysDepartment> toEntity(List<DepartmentDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysDepartment> list = new ArrayList<SysDepartment>( dtoList.size() );
        for ( DepartmentDTO departmentDTO : dtoList ) {
            list.add( toEntity( departmentDTO ) );
        }

        return list;
    }

    @Override
    public DepartmentVO toVo(SysDepartment entity) {
        if ( entity == null ) {
            return null;
        }

        DepartmentVO departmentVO = new DepartmentVO();

        departmentVO.setId( entity.getId() );
        departmentVO.setPid( entity.getPid() );
        departmentVO.setName( entity.getName() );
        departmentVO.setDeptSort( entity.getDeptSort() );
        departmentVO.setEnabled( entity.getEnabled() );
        departmentVO.setCreateTime( entity.getCreateTime() );

        return departmentVO;
    }

    @Override
    public List<DepartmentVO> toVo(List<SysDepartment> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<DepartmentVO> list = new ArrayList<DepartmentVO>( entityList.size() );
        for ( SysDepartment sysDepartment : entityList ) {
            list.add( toVo( sysDepartment ) );
        }

        return list;
    }

    @Override
    public void copyProperties(DepartmentDTO source, SysDepartment target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        target.setPid( source.getPid() );
        target.setName( source.getName() );
        target.setDeptSort( source.getDeptSort() );
        target.setEnabled( source.getEnabled() );
    }
}
