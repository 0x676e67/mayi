package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.common.log.annotation.Log;
import com.zf1976.ant.upms.biz.pojo.dto.role.RoleDTO;
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
    public ResultData<IPage<RoleVO>> selectAll() {
        return ResultData.success(this.service.selectAll());
    }

    @PostMapping("/page")
//    @Authorize("role:list")
    public ResultData<IPage<RoleVO>> selectRolePage(@RequestBody RequestPage<RoleQueryParam> requestPage) {
        return ResultData.success(service.selectRolePage(requestPage));
    }

    @Log(description = "根据id查询角色")
    @PostMapping("/{id}")
//    @Authorize("role:list")
    public ResultData<RoleVO> selectRole(@PathVariable("id") Long id) {
        return ResultData.success(service.selectRole(id));
    }

    @GetMapping("/level")
//    @Authorize("role:list")
    public ResultData<Integer> getRoleLevel() {
        return ResultData.success(service.getRoleLevel());
    }

    @Log(description = "修改角色状态")
    @PatchMapping("/status")
//    @Authorize("role:edit")
    public ResultData<Optional<Void>> setRoleStatus(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return ResultData.success(service.setRoleStatus(id, enabled));
    }

    @Log(description = "新增角色")
    @PostMapping("/save")
//    @Authorize("role:add")
    public ResultData<Optional<Void>> saveRole(@RequestBody @Validated(ValidationInsertGroup.class) RoleDTO dto) {
        return ResultData.success(service.savaRole(dto));
    }

    @Log(description = "更新角色")
    @PutMapping("/update")
//    @Authorize("role:edit")
    public ResultData<Optional<Void>> updateRole(@RequestBody @Validated(ValidationUpdateGroup.class) RoleDTO dto) {
        return ResultData.success(service.updateRole(dto));
    }

    @Log(description = "删除角色")
    @DeleteMapping("/delete")
//    @Authorize("role:del")
    public ResultData<Optional<Void>> deleteRole(@RequestBody Set<Long> ids) {
        return ResultData.success(service.deleteRole(ids));
    }
}
