package com.zf1976.mayi.auth.controller.security;

import com.zf1976.mayi.auth.pojo.ResourceLinkBinding;
import com.zf1976.mayi.auth.pojo.RoleBinding;
import com.zf1976.mayi.auth.service.PermissionBindingService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(
        value = "/oauth/security/permission"
)
public class PermissionBindingController {

   private final PermissionBindingService bindingService;

    public PermissionBindingController(PermissionBindingService bindingService) {
        this.bindingService = bindingService;
    }

    @PostMapping("/binding/role/list")
    @PreAuthorize("hasRole('admin')")
    public DataResult<List<RoleBinding>> selectBindingRoleList() {
        return DataResult.success(bindingService.selectRoleBindingList());
    }

    @PostMapping("/binding/resource/list")
    @PreAuthorize("hasRole('admin')")
    public DataResult<List<ResourceLinkBinding>> selectBindingResourceList() {
        return DataResult.success(this.bindingService.selectResourceLinkBindingList());
    }

    @PostMapping("/binding/resource")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> bindResource(@NotNull Long id, @RequestBody Set<Long> permissionList) {
        return DataResult.success(this.bindingService.bindingResource(id, permissionList));
    }

    @PostMapping("/binding/role")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> bindingRole(@NotNull Long id, @RequestBody Set<Long> permissionList) {
        return DataResult.success(this.bindingService.bindingRole(id, permissionList));
    }

    @PutMapping("/unbinding/resource")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> unbindingResource(@NotNull Long id, @RequestBody Set<Long> permissionList) {
        return DataResult.success(this.bindingService.unbindingResource(id, permissionList));
    }

    @PutMapping("/unbinding/role")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> unbindingRole(@NotNull Long id, @RequestBody Set<Long> permissionList) {
        return DataResult.success(this.bindingService.unbindingRole(id, permissionList));
    }

}
