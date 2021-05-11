package com.zf1976.ant.auth.controller;

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
@RequestMapping("/oauth/security")
public class SecurityController {

    private final OAuth2ClientService oAuth2ClientService;
    private final ResourceService resourceService;

    public SecurityController(OAuth2ClientService oAuth2ClientService, ResourceService resourceService) {
        this.oAuth2ClientService = oAuth2ClientService;
        this.resourceService = resourceService;
    }

    @PostMapping("/client/page")
    @PreAuthorize("hasAuthority('admin')")
    public DataResult<IPage<ClientDetails>> clientDetailsPage(@RequestParam Integer page, @RequestParam Integer size) {
        return DataResult.success(oAuth2ClientService.clientDetailsIPage(new Page<>(page, size)));
    }

    @DeleteMapping("/client")
    @PreAuthorize("hasAuthority('admin')")
    public DataResult<Optional<Void>> deleteClient(@RequestParam String clientId) {
        return DataResult.success(this.oAuth2ClientService.deleteClient(clientId));
    }

    @PostMapping("/resource/page")
    @PreAuthorize("hasAuthority('admin')")
    public DataResult<IPage<ResourceNode>> selectResourceByPage(@RequestBody Page<SysResource> page) {
        return DataResult.success(this.resourceService.selectResourceNodeByPage(page));
    }

}
