package com.zf1976.ant.auth.controller.security;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.auth.pojo.dto.ClientDetailsDTO;
import com.zf1976.ant.auth.pojo.vo.ClientDetailsVO;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public DataResult<Void> addClient(@RequestBody ClientDetailsDTO dto) {
        return DataResult.success(this.oAuth2ClientService.addClient(dto));
    }

    @DeleteMapping("/del")
    @PreAuthorize("hasRole('admin')")
    public DataResult<Void> deleteClient(@RequestParam String clientId) {
        return DataResult.success(this.oAuth2ClientService.deleteClient(clientId));
    }


}
