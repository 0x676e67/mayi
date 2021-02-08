package com.zf1976.ant.common.security.controller;

import com.wf.captcha.base.Captcha;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import com.zf1976.ant.common.core.foundation.Result;
import com.zf1976.ant.common.security.cache.support.CaptchaGenerator;
import com.zf1976.ant.common.security.cache.validate.service.CaptchaService;
import com.zf1976.ant.common.security.pojo.vo.CaptchaVo;
import com.zf1976.ant.common.security.safe.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:14
 */
@RestController
@RequestMapping("/auth")
public class SecurityController {

    private static final Log LOG = LogFactory.getLog(SecurityController.class);
    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    private final CaptchaService captchaService;
    private final CaptchaProperties captchaConfig;

    public SecurityController(CaptchaService captchaService, CaptchaProperties captchaConfig) {
        this.captchaService = captchaService;
        this.captchaConfig = captchaConfig;
    }

    @GetMapping("/code")
    public ResponseEntity<CaptchaVo> getVerifyCode() {
        // 获取验证码
        Captcha captcha = CaptchaGenerator.getCaptcha();
        // 生产uuid
        UUID uuid = ALTERNATIVE_JDK_ID_GENERATOR.generateId();
        //将验证码保存在 redis 缓存中
        boolean isSave = captchaService.sendCaptcha(uuid.toString(),
                                                    captcha.text(),
                                                    captchaConfig.getExpiration(),
                                                    TimeUnit.MILLISECONDS);
        if (isSave) {
            LOG.info("Generator Captcha is：" + captcha.text());
        } else {
            LOG.info("Captcha not saved!");
        }
        final CaptchaVo captchaVo = CaptchaVo.builder()
                                             .img(captcha.toBase64())
                                             .uuid(uuid.toString())
                                             .build();
        return ResponseEntity.ok(captchaVo);
    }

    @GetMapping("/info")
    public Result<UserDetails> getUserInfo(){
        return Result.success(SecurityContextHolder.getDetails());
    }
}
