package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.upms.biz.convert.base.Convert;
import com.zf1976.mayi.upms.biz.pojo.dto.menu.MenuDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysMenu;
import com.zf1976.mayi.upms.biz.pojo.vo.menu.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2020/10/23 3:00 下午
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysMenuConvert extends Convert<SysMenu, MenuVO, MenuDTO> {
    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);
}
