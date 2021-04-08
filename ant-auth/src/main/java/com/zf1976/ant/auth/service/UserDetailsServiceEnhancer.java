package com.zf1976.ant.auth.service;

import com.zf1976.ant.common.security.pojo.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * {@link UserDetailsService} 增强接口
 *
 * @author mac
 * @date 2021/4/8
 */
public interface UserDetailsServiceEnhancer extends UserDetailsService {

    /**
     * 用户细节
     *
     * @return /
     */
    UserDetails userDetails();
}
