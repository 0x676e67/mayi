package com.zf1976.ant.upms.biz.rpc;

import com.zf1976.ant.common.core.foundation.DataResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author ant
 * Create by Ant on 2021/3/28 5:03 PM
 */
@FeignClient(name = "ant-auth")
public interface SecurityClient {

    /**
     * 远程调用服务登出处理
     *
     * @param token 请求头token
     * @return /
     */
    @PostMapping("/oauth/logout")
    @SuppressWarnings("rawtypes")
    DataResult logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token);
}
