package com.zf1976.ant.auth.controller;

import com.wf.captcha.base.Captcha;
import com.zf1976.ant.auth.cache.support.CaptchaGenerator;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import com.zf1976.ant.common.core.foundation.Result;
import com.zf1976.ant.auth.cache.validate.service.CaptchaService;
import com.zf1976.ant.auth.pojo.vo.CaptchaVo;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.core.util.RequestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.windows.ThemeReader;

import java.security.KeyPair;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:14
 */
@RestController
@RequestMapping("/oauth")
public class Oauth2Controller {

    private static final Log LOG = LogFactory.getLog(Oauth2Controller.class);
    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    private final CaptchaService captchaService;
    private final CaptchaProperties captchaConfig;

    public Oauth2Controller(CaptchaService captchaService,
                            CaptchaProperties captchaConfig) {
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

    @GetMapping("/test")
     public void test(){
        String header = RequestUtils.getRequest().getParameter("token");
        RedisTokenStore redisTokenStore = SecurityContextHolder.get(RedisTokenStore.class);
        OAuth2AccessToken oAuth2AccessToken = redisTokenStore.readAccessToken(header);
        OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
        redisTokenStore.removeAccessToken(oAuth2AccessToken);
        redisTokenStore.removeRefreshToken(refreshToken);

    }
}
