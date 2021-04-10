package com.zf1976.ant.auth.endpoint;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.common.core.foundation.DataResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mac
 * @date 2021/4/10
 */
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final OAuth2ClientService oAuth2ClientService;

    public SecurityController(OAuth2ClientService oAuth2ClientService) {
        this.oAuth2ClientService = oAuth2ClientService;
    }

    @PostMapping("/client/page")
    public DataResult<IPage<ClientDetails>> clientDetailsPage(@RequestParam Integer page, @RequestParam Integer size) {
        return DataResult.success(oAuth2ClientService.clientDetailsIPage(new Page<>(page, size)));
    }

}
