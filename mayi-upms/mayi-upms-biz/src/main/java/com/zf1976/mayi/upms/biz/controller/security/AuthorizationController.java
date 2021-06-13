package com.zf1976.mayi.upms.biz.controller.security;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.communication.Inner;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.service.SysUserService;
import org.springframework.web.bind.annotation.*;


/**
 * @author mac
 * @date 2021/6/13
 */
@RestController
@RequestMapping("/api/authorities")
public class AuthorizationController {

    private final SysUserService userService;

    public AuthorizationController(SysUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/{username}")
    @Inner
    public DataResult<User> getUser(@PathVariable("username")String username) {
        return DataResult.success(this.userService.findUserByUsername(username));
    }
}
