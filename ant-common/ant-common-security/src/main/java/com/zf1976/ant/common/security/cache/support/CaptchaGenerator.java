package com.zf1976.ant.common.security.cache.support;


import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.core.property.CaptchaProperties;
import com.zf1976.ant.common.core.property.enums.CaptchaTypeEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.awt.*;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午12:37
 */
@Data
public class CaptchaGenerator {

    private static CaptchaProperties captchaProperties;


    /**
     * 获取验证码对象
     *
     * @return 验证码
     */
    public static Captcha getCaptcha() {
        if (StringUtils.isEmpty(captchaProperties)) {
            captchaProperties = SpringContextHolder.getBean(CaptchaProperties.class);
            if (StringUtils.isEmpty(captchaProperties.getCodeType())) {
                captchaProperties.setCodeType(CaptchaTypeEnum.ARITHMETIC);
            }
        }
        return generatedCaptcha(captchaProperties);
    }


    /**
     * 根据配置生产验证码
     *
     * @param config 验证码属性
     * @return 验证码
     */
    private static Captcha generatedCaptcha(CaptchaProperties config) {
        Captcha captcha;
        switch (config.getCodeType()) {
            case CHINESE:
                captcha = new ChineseCaptcha(config.getWidth(),
                                             config.getHeight(),
                                             config.getLength());
                break;
            case CHINESE_GIF:
                captcha = new ChineseGifCaptcha(config.getWidth(),
                                                config.getHeight(),
                                                config.getLength());
                break;
            case GIF:
                captcha = new GifCaptcha(config.getWidth(),
                                         config.getHeight(),
                                         config.getLength());
                break;

            case SPEC:
                captcha = new SpecCaptcha(config.getWidth(),
                                          config.getHeight(),
                                          config.getLength());
                break;
            default:
                captcha = new ArithmeticCaptcha(config.getWidth(),
                                                config.getHeight(),
                                                config.getLength());
                break;
        }

        if (StringUtils.isEmpty(config.getFontName())) {
            captcha.setFont(new Font(Font.MONOSPACED,
                                     Font.PLAIN,
                                     config.getFontSize()));
        } else {
            captcha.setFont(new Font(config.getFontName(),
                                     Font.PLAIN,
                                     config.getFontSize()));
        }
        return captcha;
    }
}
