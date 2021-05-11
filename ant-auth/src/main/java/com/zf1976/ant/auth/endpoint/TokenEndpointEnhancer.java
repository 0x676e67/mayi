package com.zf1976.ant.auth.endpoint;

import com.wf.captcha.base.Captcha;
import com.zf1976.ant.auth.LoginDetails;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.auth.service.UserDetailsServiceEnhancer;
import com.zf1976.ant.common.component.validate.service.CaptchaService;
import com.zf1976.ant.common.component.validate.support.CaptchaGenerator;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.core.util.RequestUtil;
import com.zf1976.ant.common.security.pojo.Details;
import com.zf1976.ant.common.security.pojo.vo.CaptchaVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:14
 */
@RestController
@RequestMapping("/oauth")
public class TokenEndpointEnhancer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    private final CaptchaService captchaService;
    private final UserDetailsServiceEnhancer userDetailsService;
    private final TokenEndpoint tokenEndpoint;

    public TokenEndpointEnhancer(CaptchaService captchaService, UserDetailsServiceEnhancer userDetailsService, TokenEndpoint tokenEndpoint) {
        this.captchaService = captchaService;
        this.userDetailsService = userDetailsService;
        this.tokenEndpoint = tokenEndpoint;
    }

    @GetMapping("/token")
    public DataResult<OAuth2AccessToken> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException("GET");
    }

    @PostMapping("/token")
    public DataResult<LoginDetails> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!(principal instanceof Authentication)) {
            throw new InsufficientAuthenticationException("There is no client authentication. Try adding an appropriate authentication filter.");
        } else {
            ResponseEntity<OAuth2AccessToken> responseEntity = this.tokenEndpoint.postAccessToken(principal, parameters);

            OAuth2AccessToken oAuth2AccessToken = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful() && oAuth2AccessToken != null) {
                String username = (String) oAuth2AccessToken.getAdditionalInformation().get(AuthConstants.USERNAME);
                Details details = userDetailsService.selectUserDetails(username);
                RequestUtil.getRequest().setAttribute(AuthConstants.DETAILS, userDetailsService.selectUserDetails(username));
                RequestUtil.getRequest().setAttribute(AuthConstants.SESSION_EXPIRED, oAuth2AccessToken.getExpiresIn());
                SecurityContextHolder.createSession(oAuth2AccessToken);
                final LoginDetails loginDetails = LoginDetails.LoginDetailsBuilder.builder()
                                                                           .details(details)
                                                                           .oAuth2AccessToken(oAuth2AccessToken)
                                                                           .build();
                return DataResult.success(loginDetails);
            }
            throw new InsufficientAuthenticationException("Client authentication failed.");
        }
    }

    @PostMapping("/info")
    public DataResult<Details> getUserDetails(){
        return DataResult.success(userDetailsService.selectUserDetails());
    }

    @GetMapping("/code")
    public ResponseEntity<CaptchaVo> getVerifyCode() {
        // 获取验证码
        Captcha captcha = CaptchaGenerator.getCaptcha();
        // 生成uuid
        UUID uuid = ALTERNATIVE_JDK_ID_GENERATOR.generateId();
        //将验证码保存在 redis 缓存中
        boolean isSave = captchaService.storeCaptcha(uuid.toString(), captcha.text());
        if (isSave) {
            if (logger.isDebugEnabled()) {
                logger.info("Generator Captcha is：" + captcha.text());
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.info("Captcha：{} not saved.", captcha.text());
            }
        }
        final CaptchaVo captchaVo = CaptchaVo.builder()
                                             .img(captcha.toBase64())
                                             .uuid(uuid.toString())
                                             .build();
        return ResponseEntity.ok(captchaVo);
    }

}
