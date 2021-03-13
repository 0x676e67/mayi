package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.component.mail.ValidateFactory;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.common.log.annotation.Log;

import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateEmailDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UpdatePasswordDTO;
import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.query.UserQueryParam;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.ant.upms.biz.service.SysUserService;
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
//    @Authorize("user:list")
    public ResultData<IPage<UserVO>> selectUserPage(@RequestBody RequestPage<UserQueryParam> requestPage) {
        return ResultData.success(service.selectUserPage(requestPage));
    }

    @Log(description = "添加用户")
    @PostMapping("/save")
//    @Authorize("user:add")
    public ResultData<Optional<Void>> saveUser(@RequestBody @Validated(ValidationInsertGroup.class) UserDTO dto) {
        return ResultData.success(service.saveUser(dto));
    }

    @Log(description = "更新用户")
    @PutMapping("/update")
//    @Authorize("user:edit")
    public ResultData<Optional<Void>> updateUser(@RequestBody @Validated(ValidationUpdateGroup.class) UserDTO dto) {
        return ResultData.success(service.updateUser(dto));
    }

    @Log(description = "删除用户")
    @DeleteMapping("/delete")
//    @Authorize("user:del")
    public ResultData<Optional<Void>> deleteUser(@RequestBody Set<Long> ids) {
        return ResultData.success(service.deleteUser(ids));
    }

    @PostMapping("/position/{id}")
////    @Authorize("user:list")
    public ResultData<Set<Long>> getUserPositionIds(@PathVariable Long id) {
        return ResultData.success(service.selectUserPositionIds(id));
    }

    @PostMapping("/role/{id}")
//    @Authorize("user:list")
    public ResultData<Set<Long>> getUserRoleIds(@PathVariable Long id) {
        return ResultData.success(service.selectUserRoleIds(id));
    }

    @Log(description = "修改用户状态")
    @PatchMapping("/status")
    public ResultData<Optional<Void>> setUserStatus(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return ResultData.success(service.setUserStatus(id, enabled));
    }

    @PostMapping("/update/avatar")
    public ResultData<Optional<Void>> updateAvatar(@RequestParam("avatar") MultipartFile multipartFile) {
        return ResultData.success(service.updateAvatar(multipartFile));
    }

    @PatchMapping("/update/password")
    public ResultData<Optional<Void>> updatePass(@RequestBody @Validated UpdatePasswordDTO dto) {
        return ResultData.success(service.updatePassword(dto));
    }

    @PatchMapping("/update/email/{code}")
    public ResultData<Optional<Void>> updateEmail(@PathVariable String code, @RequestBody @Validated UpdateEmailDTO dto) {
        return ResultData.success(service.updateEmail(code, dto));
    }

    @GetMapping("/email/verify-code")
    public ResultData<Optional<Void>> getEmailVerifyCode(@RequestParam String email) {
        return ResultData.success(ValidateFactory.getInstance().sendMailValidate(email));
    }

    @PatchMapping("/update/info")
    public ResultData<Optional<Void>> updateInfo(@RequestBody @Validated UpdateInfoDTO dto) {
        return ResultData.success(service.updateInfo(dto));
    }
}
