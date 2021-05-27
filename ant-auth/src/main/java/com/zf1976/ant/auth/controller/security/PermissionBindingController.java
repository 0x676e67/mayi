package com.zf1976.ant.auth.controller.security;

import com.zf1976.ant.auth.service.PermissionBindingService;
import com.zf1976.ant.common.core.foundation.DataResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("/oauth/security/permission")
public class PermissionBindingController {

   private final PermissionBindingService bindingService;

    public PermissionBindingController(PermissionBindingService bindingService) {
        this.bindingService = bindingService;
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
