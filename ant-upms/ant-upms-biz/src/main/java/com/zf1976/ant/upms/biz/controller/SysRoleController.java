package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.log.annotation.Log;
import com.zf1976.ant.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.upms.biz.pojo.query.RoleQueryParam;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.role.RoleVO;
import com.zf1976.ant.upms.biz.service.SysRoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/22
 **/
@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    private final SysRoleService service;

    public SysRoleController(SysRoleService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public DataResult<IPage<RoleVO>> selectAll() {
        return DataResult.success(this.service.selectAll());
    }

    @PostMapping("/page")
//    @Authorize("role:list")
    public DataResult<IPage<RoleVO>> selectRolePage(@RequestBody Query<RoleQueryParam> requestPage) {
        return DataResult.success(service.selectRolePage(requestPage));
    }

    @Log(description = "根据id查询角色")
    @PostMapping("/{id}")
//    @Authorize("role:list")
    public DataResult<RoleVO> selectRole(@PathVariable("id") @NotNull Long id) {
        return DataResult.success(service.selectRole(id));
    }

    @GetMapping("/level")
//    @Authorize("role:list")
    public DataResult<Integer> getRoleLevel() {
        return DataResult.success(service.selectRoleLevel());
    }

    @Log(description = "修改角色状态")
    @PatchMapping("/status")
//    @Authorize("role:edit")
    public DataResult<Optional<Void>> setRoleStatus(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return DataResult.success(service.updateRoleStatus(id, enabled));
    }

    @Log(description = "新增角色")
    @PostMapping("/save")
//    @Authorize("role:add")
    public DataResult<Optional<Void>> saveRole(@RequestBody @Validated(ValidationInsertGroup.class) RoleDTO dto) {
        return DataResult.success(service.savaRole(dto));
    }

    @Log(description = "更新角色")
    @PutMapping("/update")
//    @Authorize("role:edit")
    public DataResult<Optional<Void>> updateRole(@RequestBody @Validated(ValidationUpdateGroup.class) RoleDTO dto) {
        return DataResult.success(service.updateRole(dto));
    }

    @Log(description = "删除角色")
    @DeleteMapping("/delete")
//    @Authorize("role:del")
    public DataResult<Optional<Void>> deleteRole(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteRole(ids));
    }
}
