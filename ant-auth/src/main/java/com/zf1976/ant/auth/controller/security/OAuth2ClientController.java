package com.zf1976.ant.auth.controller.security;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.auth.pojo.dto.ClientDetailsDTO;
import com.zf1976.ant.auth.pojo.vo.ClientDetailsVO;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @author mac
 * @date 2021/4/10
 */
@RestController
@RequestMapping(
        value = "/oauth/security/client"
)
public class OAuth2ClientController {

    private final OAuth2ClientService oAuth2ClientService;

    public OAuth2ClientController(OAuth2ClientService oAuth2ClientService) {
        this.oAuth2ClientService = oAuth2ClientService;
    }

    @PostMapping("/page")
    @PreAuthorize("hasRole('admin')")
    public DataResult<IPage<ClientDetailsVO>> clientDetailsPage(@RequestBody Query<?> page) {
        return DataResult.success(oAuth2ClientService.clientDetailsPage(page));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> addClient(@RequestBody @Validated ClientDetailsDTO dto) {
        return DataResult.success(this.oAuth2ClientService.addClient(dto));
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> editClient(@RequestBody @Validated ClientDetailsDTO dto) {
        return DataResult.success(this.oAuth2ClientService.editClient(dto));
    }

    @DeleteMapping("/del")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteClient(@RequestParam @NotBlank String clientId) {
        return DataResult.success(this.oAuth2ClientService.deleteClient(clientId));
    }

    @DeleteMapping("/del/batch")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteBatchClient(@RequestBody @NonNull Set<String> clientIdList) {
        return DataResult.success(this.oAuth2ClientService.deleteBatchClient(clientIdList));
    }


}
