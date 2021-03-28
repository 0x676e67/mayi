package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.menu.MenuDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysMenu;
import com.zf1976.ant.upms.biz.pojo.vo.menu.MenuVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T19:16:40+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
public class SysMenuConvertImpl implements SysMenuConvert {

    @Override
    public SysMenu toEntity(MenuDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysMenu sysMenu = new SysMenu();

        sysMenu.setId( dto.getId() );
        sysMenu.setPid( dto.getPid() );
        sysMenu.setType( dto.getType() );
        sysMenu.setTitle( dto.getTitle() );
        sysMenu.setComponentName( dto.getComponentName() );
        sysMenu.setComponentPath( dto.getComponentPath() );
        sysMenu.setMenuSort( dto.getMenuSort() );
        sysMenu.setIcon( dto.getIcon() );
        sysMenu.setRoutePath( dto.getRoutePath() );
        sysMenu.setIframe( dto.getIframe() );
        sysMenu.setCache( dto.getCache() );
        sysMenu.setHidden( dto.getHidden() );
        sysMenu.setPermission( dto.getPermission() );

        return sysMenu;
    }

    @Override
    public List<SysMenu> toEntity(List<MenuDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysMenu> list = new ArrayList<SysMenu>( dtoList.size() );
        for ( MenuDTO menuDTO : dtoList ) {
            list.add( toEntity( menuDTO ) );
        }

        return list;
    }

    @Override
    public MenuVO toVo(SysMenu entity) {
        if ( entity == null ) {
            return null;
        }

        MenuVO menuVO = new MenuVO();

        menuVO.setId( entity.getId() );
        menuVO.setChildren( toVo( entity.getChildren() ) );
        menuVO.setPid( entity.getPid() );
        menuVO.setType( entity.getType() );
        menuVO.setTitle( entity.getTitle() );
        menuVO.setComponentName( entity.getComponentName() );
        menuVO.setComponentPath( entity.getComponentPath() );
        menuVO.setMenuSort( entity.getMenuSort() );
        menuVO.setIcon( entity.getIcon() );
        menuVO.setRoutePath( entity.getRoutePath() );
        menuVO.setIframe( entity.getIframe() );
        menuVO.setCache( entity.getCache() );
        menuVO.setHidden( entity.getHidden() );
        menuVO.setPermission( entity.getPermission() );
        menuVO.setCreateTime( entity.getCreateTime() );

        return menuVO;
    }

    @Override
    public List<MenuVO> toVo(List<SysMenu> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<MenuVO> list = new ArrayList<MenuVO>( entityList.size() );
        for ( SysMenu sysMenu : entityList ) {
            list.add( toVo( sysMenu ) );
        }

        return list;
    }

    @Override
    public void copyProperties(MenuDTO source, SysMenu target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        target.setPid( source.getPid() );
        target.setType( source.getType() );
        target.setTitle( source.getTitle() );
        target.setComponentName( source.getComponentName() );
        target.setComponentPath( source.getComponentPath() );
        target.setMenuSort( source.getMenuSort() );
        target.setIcon( source.getIcon() );
        target.setRoutePath( source.getRoutePath() );
        target.setIframe( source.getIframe() );
        target.setCache( source.getCache() );
        target.setHidden( source.getHidden() );
        target.setPermission( source.getPermission() );
    }

    @Override
    public MenuVO clone(MenuVO source, MenuVO target) {
        if ( source == null ) {
            return null;
        }

        target.setId( source.getId() );
        if ( target.getChildren() != null ) {
            List<MenuVO> list = source.getChildren();
            if ( list != null ) {
                target.getChildren().clear();
                target.getChildren().addAll( list );
            }
            else {
                target.setChildren( null );
            }
        }
        else {
            List<MenuVO> list = source.getChildren();
            if ( list != null ) {
                target.setChildren( new ArrayList<MenuVO>( list ) );
            }
        }
        target.setPid( source.getPid() );
        target.setType( source.getType() );
        target.setTitle( source.getTitle() );
        target.setComponentName( source.getComponentName() );
        target.setComponentPath( source.getComponentPath() );
        target.setMenuSort( source.getMenuSort() );
        target.setHasChildren( source.getHasChildren() );
        target.setLeaf( source.getLeaf() );
        target.setIcon( source.getIcon() );
        target.setRoutePath( source.getRoutePath() );
        target.setIframe( source.getIframe() );
        target.setCache( source.getCache() );
        target.setHidden( source.getHidden() );
        target.setPermission( source.getPermission() );
        target.setCreateTime( source.getCreateTime() );

        return target;
    }
}
