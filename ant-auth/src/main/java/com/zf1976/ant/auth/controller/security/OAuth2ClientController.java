package com.zf1976.ant.auth.controller.security;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.auth.pojo.po.SysResource;
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

    @DeleteMapping
    @PreAuthorize("hasRole('admin')")
    public DataResult<Optional<Void>> deleteClient(@RequestParam String clientId) {
        return DataResult.success(this.oAuth2ClientService.deleteClient(clientId));
    }



}
