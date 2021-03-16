package com.zf1976.ant.gateway.feign;

import com.zf1976.ant.common.core.foundation.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ant
 * Create by Ant on 2021/3/16 7:39 PM
 */
@FeignClient("ant-auth")
public interface ClientTest {

    @GetMapping("oauth/token_key")
    ResultData<?> test();
}
