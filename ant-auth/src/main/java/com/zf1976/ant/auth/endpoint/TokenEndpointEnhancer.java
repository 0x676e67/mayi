package com.zf1976.ant.auth.endpoint;

import com.wf.captcha.base.Captcha;
import com.zf1976.ant.common.component.session.Session;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.property.CaptchaProperties;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.security.AntUserDetails;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.component.session.SessionContextHolder;
import com.zf1976.ant.common.security.cache.support.CaptchaGenerator;
import com.zf1976.ant.common.security.cache.validate.service.CaptchaService;
import com.zf1976.ant.common.security.pojo.vo.CaptchaVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:14
 */
@RestController
@RequestMapping("/oauth")
public class TokenEndpointEnhancer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    private final WebResponseExceptionTranslator<OAuth2Exception> providerExceptionHandler = new DefaultWebResponseExceptionTranslator();
    private final CaptchaService captchaService;
    private final CaptchaProperties captchaConfig;
    private final TokenEndpoint tokenEndpoint;

    public TokenEndpointEnhancer(CaptchaService captchaService,
                                 CaptchaProperties captchaConfig,
                                 TokenEndpoint tokenEndpoint
    ) {
        this.captchaService = captchaService;
        this.captchaConfig = captchaConfig;
        this.tokenEndpoint = tokenEndpoint;
    }

    @GetMapping("/token")
    public ResultData<OAuth2AccessToken> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException("GET");
    }

    @PostMapping("/token")
    public ResultData<OAuth2AccessToken> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!(principal instanceof Authentication)) {
            throw new InsufficientAuthenticationException("There is no client authentication. Try adding an appropriate authentication filter.");
        } else {
            ResponseEntity<OAuth2AccessToken> responseEntity = this.tokenEndpoint.postAccessToken(principal, parameters);
            OAuth2AccessToken oAuth2AccessToken = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful() && oAuth2AccessToken != null) {
                // 保存登录会话
                this.saveSessionState(oAuth2AccessToken);
                return ResultData.success(oAuth2AccessToken);
            }
            throw new InsufficientAuthenticationException("Client authentication failed.");
        }
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
            logger.info("Generator Captcha is：" + captcha.text());
        } else {
            logger.info("Captcha not saved!");
        }
        final CaptchaVo captchaVo = CaptchaVo.builder()
                                             .img(captcha.toBase64())
                                             .uuid(uuid.toString())
                                             .build();
        return ResponseEntity.ok(captchaVo);
    }

    @GetMapping("/info")
    public ResultData<UserDetails> getUserInfo(){
        return ResultData.success(SecurityContextHolder.getDetails());
    }

    public void saveSessionState(OAuth2AccessToken oAuth2AccessToken) {
        // 获取token
        String tokenValue = oAuth2AccessToken.getValue();
        // 获取token过期时间
        Integer expiration = oAuth2AccessToken.getExpiresIn();
        // 设置token过期时间
        RequestUtils.getRequest()
                    .setAttribute(AuthConstants.EXPIRED, expiration);
        // 构建session
        var session = SecurityContextHolder.generatedSession(tokenValue);
        // 保存会话
        SessionContextHolder.storeSession(tokenValue, session);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<OAuth2Exception> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) throws Exception {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }
        return this.providerExceptionHandler.translate(e);
    }

    @ExceptionHandler({ClientRegistrationException.class})
    public ResponseEntity<OAuth2Exception> handleClientRegistrationException(Exception e) throws Exception {
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }
        return this.providerExceptionHandler.translate(new BadClientCredentialsException());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }
        return this.providerExceptionHandler.translate(e);
    }

    @ExceptionHandler({OAuth2Exception.class})
    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }
        return this.providerExceptionHandler.translate(e);
    }

}
