package com.zf1976.ant.auth.controller.security;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.ClientDetailsDTO;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.common.core.foundation.DataResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public DataResult<IPage<ClientDetails>> clientDetailsPage(@RequestBody Page<ClientDetails> page) {
        return DataResult.success(oAuth2ClientService.clientDetailsIPage(page));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> addClient(@RequestBody ClientDetailsDTO dto) {
        return DataResult.success(this.oAuth2ClientService.addClient(dto));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteClient(@RequestParam String clientId) {
        return DataResult.success(this.oAuth2ClientService.deleteClient(clientId));
    }


}
