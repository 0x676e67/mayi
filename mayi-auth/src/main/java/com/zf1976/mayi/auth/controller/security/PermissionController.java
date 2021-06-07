package com.zf1976.mayi.auth.controller.security;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.auth.pojo.dto.PermissionDTO;
import com.zf1976.mayi.auth.pojo.vo.PermissionVO;
import com.zf1976.mayi.auth.service.PermissionService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 * @date 2021/5/12
 */
@RestController
@RequestMapping(
        value = "/oauth/security/permission"
)
public class PermissionController {

    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('admin')")
    public DataResult<IPage<PermissionVO>> selectPermissionPage(@RequestBody Query<?> query) {
        return DataResult.success(this.service.selectPermissionByPage(query));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('admin')")
    DataResult<Void> savaPermission(@RequestBody @Validated(ValidationInsertGroup.class) PermissionDTO permissionDTO) {
        return DataResult.success(this.service.savePermission(permissionDTO));
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('admin')")
    DataResult<Void> editPermission(@RequestBody @Validated(ValidationUpdateGroup.class) PermissionDTO permissionDTO) {
        return DataResult.success(this.service.updatePermission(permissionDTO));
    }

    @DeleteMapping("/del")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deletePermission(@RequestParam @NotNull Long id) {
        return DataResult.success(this.service.deletePermissionById(id));
    }

    @DeleteMapping("/del/batch")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deletePermission(@RequestBody Set<Long> ids) {
        return DataResult.success(this.service.deletePermissionByIds(ids));
    }


}
