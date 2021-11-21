package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.auth.oauth2.repository.Page;
import com.zf1976.mayi.auth.pojo.dto.RegisteredClientDTO;
import com.zf1976.mayi.auth.pojo.vo.RegisteredClientVO;
import com.zf1976.mayi.auth.service.OAuth2RegisteredClientService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("/security/clients")
public class RegisteredClientController {

    private final OAuth2RegisteredClientService oAuth2RegisteredClientService;


    public RegisteredClientController(OAuth2RegisteredClientService oAuth2RegisteredClientService) {
        this.oAuth2RegisteredClientService = oAuth2RegisteredClientService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Page<RegisteredClientVO>> findByPage(@RequestBody @NotNull Page<?> page) {
        return DataResult.success(this.oAuth2RegisteredClientService.findList(page));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> sava(@RequestBody @Validated(ValidationInsertGroup.class) RegisteredClientDTO dto) {
        return DataResult.success(this.oAuth2RegisteredClientService.sava(dto));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> update(@RequestBody @Validated(ValidationUpdateGroup.class) RegisteredClientDTO dto) {
        return DataResult.success(this.oAuth2RegisteredClientService.sava(dto));
    }

    @DeleteMapping("/delete/id/{id}")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteById(@PathVariable String id) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteById(id));
    }

    @DeleteMapping("/delete/id")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteByIds(@RequestBody Set<String> ids) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteByIds(ids));
    }

    @DeleteMapping("/delete/client_id/{clientId}")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteByClientId(@PathVariable String clientId) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteByClientId(clientId));
    }

    @DeleteMapping("/delete/client_id")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteByClientIds(@RequestBody Set<String> clientIds) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteByClientIds(clientIds));
    }

}
