package com.zf1976.ant.auth.service;

import com.zf1976.ant.common.security.pojo.Details;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * {@link UserDetailsService} 增强接口
 *
 * @author mac
 * @date 2021/4/8
 */
public interface UserDetailsServiceEnhancer extends UserDetailsService {

    /**
     * 查询当前用户细节
     *
     * @return /
     */
    Details selectUserDetails();

    /**
     * 根据用户名查询用户认证细节
     *
     * @param username 用户名
     * @return /
     */
    Details selectUserDetails(String username);
}
