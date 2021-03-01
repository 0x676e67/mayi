package com.zf1976.ant.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author mac
 * @date 2021/3/1
 **/
@FeignClient("ant-auth")
public interface SecurityFeignService {
}
