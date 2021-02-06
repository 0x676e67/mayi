package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.component.mail.ValidateFactory;
import com.zf1976.ant.common.log.annotation.Log;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateEmailDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdatePasswordDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.query.UserQueryParam;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.ant.upms.biz.service.SysUserService;
import com.zf1976.ant.common.core.foundation.Result;
import com.zf1976.ant.common.security.safe.annotation.Authorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 */
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    private final SysUserService service;

    public SysUserController(SysUserService service) {
        this.service = service;
    }

    @PostMapping("/page")
    @Authorize("user:list")
    public Result<IPage<UserVO>> selectUserPage(@RequestBody RequestPage<UserQueryParam> requestPage) {
        return Result.success(service.selectUserPage(requestPage));
    }

    @Log(description = "添加用户")
    @PostMapping("/save")
    @Authorize("user:add")
    public Result<Optional<Void>> saveUser(@RequestBody @Validated(ValidationInsertGroup.class) UserDTO dto) {
        return Result.success(service.saveUser(dto));
    }

    @Log(description = "更新用户")
    @PutMapping("/update")
    @Authorize("user:edit")
    public Result<Optional<Void>> updateUser(@RequestBody @Validated(ValidationUpdateGroup.class) UserDTO dto) {
        return Result.success(service.updateUser(dto));
    }

    @Log(description = "删除用户")
    @DeleteMapping("/delete")
    @Authorize("user:del")
    public Result<Optional<Void>> deleteUser(@RequestBody Set<Long> ids) {
        return Result.success(service.deleteUser(ids));
    }

    @PostMapping("/position/{id}")
    @Authorize("user:list")
    public Result<Set<Long>> getUserPositionIds(@PathVariable Long id) {
        return Result.success(service.selectUserPositionIds(id));
    }

    @PostMapping("/role/{id}")
    @Authorize("user:list")
    public Result<Set<Long>> getUserRoleIds(@PathVariable Long id) {
        return Result.success(service.selectUserRoleIds(id));
    }

    @Log(description = "修改用户状态")
    @PatchMapping("/status")
    public Result<Optional<Void>> setUserStatus(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return Result.success(service.setUserStatus(id, enabled));
    }

    @PostMapping("/update/avatar")
    public Result<Optional<Void>> updateAvatar(@RequestParam("avatar") MultipartFile multipartFile) {
        return Result.success(service.updateAvatar(multipartFile));
    }

    @PatchMapping("/update/password")
    public Result<Optional<Void>> updatePass(@RequestBody @Validated UpdatePasswordDTO dto) {
        return Result.success(service.updatePassword(dto));
    }

    @PatchMapping("/update/email/{code}")
    public Result<Optional<Void>> updateEmail(@PathVariable String code, @RequestBody @Validated UpdateEmailDTO dto) {
        return Result.success(service.updateEmail(code, dto));
    }

    @GetMapping("/email/verify-code")
    public Result<Optional<Void>> getEmailVerifyCode(@RequestParam String email) {
        return Result.success(ValidateFactory.getInstance().sendMailValidate(email));
    }

    @PatchMapping("/update/info")
    public Result<Optional<Void>> updateInfo(@RequestBody @Validated UpdateInfoDTO dto) {
        return Result.success(service.updateInfo(dto));
    }
}
