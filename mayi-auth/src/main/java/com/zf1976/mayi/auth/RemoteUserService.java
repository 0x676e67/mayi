package com.zf1976.mayi.auth;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.constants.SecurityConstants;
import com.zf1976.mayi.common.core.constants.ServiceNameConstants;
import com.zf1976.mayi.upms.biz.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author mac
 * @date 2021/6/13
 */
@FeignClient(ServiceNameConstants.UMPS_SERVICE)
public interface RemoteUserService {

    @PostMapping("/api/authorities/user/{username}")
    DataResult<User> getUser(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM) String form);
}
