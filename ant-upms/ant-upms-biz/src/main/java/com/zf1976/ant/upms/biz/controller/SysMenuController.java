package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.dto.menu.MenuDTO;
import com.zf1976.ant.upms.biz.pojo.query.MenuQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.menu.MenuBuildVO;
import com.zf1976.ant.upms.biz.pojo.vo.menu.MenuVO;
import com.zf1976.ant.upms.biz.service.SysMenuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 */
@RestController
@RequestMapping("/api/menus")
public class SysMenuController {

    private final SysMenuService service;

    public SysMenuController(SysMenuService service) {
        this.service = service;
    }

    @GetMapping("/build")
    public DataResult<Collection<MenuBuildVO>> buildMenuRoute() {
        return DataResult.success(service.generatedMenu());
    }

    @PostMapping("/page")
//    @Authorize("menu:list")
    public DataResult<IPage<MenuVO>> selectMenuPage(@RequestBody RequestPage<MenuQueryParam> requestPage) {
        return DataResult.success(service.selectMenuPage(requestPage));
    }

    @PostMapping("/vertex/{id}")
//    @Authorize("menu:list")
    public DataResult<IPage<MenuVO>> menuVertex(@PathVariable @NotNull Long id) {
        return DataResult.success(service.selectMenuVertex(id));
    }

    @PostMapping("/save")
//    @Authorize("menu:add")
    public DataResult<Optional<Void>> saveMenu(@RequestBody @Validated(ValidationInsertGroup.class) MenuDTO dto) {
        return DataResult.success(service.saveMenu(dto));
    }

    @PutMapping("/update")
//    @Authorize("menu:edit")
    public DataResult<Optional<Void>> updateMenu(@RequestBody @Validated(ValidationUpdateGroup.class) MenuDTO dto) {
        return DataResult.success(service.updateMenu(dto));
    }

    @DeleteMapping("/delete")
//    @Authorize("menu:del")
    public DataResult<Optional<Void>> deleteMenu(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteMenuList(ids));
    }
}
