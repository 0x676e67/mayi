package com.zf1976.ant.upms.biz.feign;

import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.security.pojo.Details;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

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
    DataResult logout();

    /**
     * 获取用户细节
     *
     * @param token token
     * @param username username
     * @return /
     */
    @PostMapping("/oauth/info")
    DataResult<Details> getUserDetails();
}
