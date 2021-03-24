package com.zf1976.ant.common.component.mail.impl;

import com.power.common.util.RandomUtil;
import com.power.common.util.ValidateUtil;
import com.zf1976.ant.common.component.mail.MailSenderProvider;
import com.zf1976.ant.common.component.mail.ValidateService;
import com.zf1976.ant.common.component.mail.pojo.ToolEmailConfig;
import com.zf1976.ant.common.core.foundation.exception.BadBusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.core.util.RedisUtils;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.security.property.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证服务
 *
 * @author mac
 */
public class ValidateServiceImpl implements ValidateService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateServiceImpl.class);

    private final static EmailProperties PROPERTIES;

    private final static TemplateEngine TEMPLATE_ENGINE;

    private ValidateServiceImpl(){}

    static {
        PROPERTIES = SpringContextHolder.getBean(EmailProperties.class);
        TEMPLATE_ENGINE = SpringContextHolder.getBean(TemplateEngine.class);
    }

    public static ValidateService getInterface() {
        return new ValidateServiceImpl();
    }

    @Override
    public Void sendMailValidate(String email) {
        if (StringUtils.isEmpty(email) || !ValidateUtil.isEmail(email)) {
            throw new BadBusinessException(BusinessMsgState.EMAIL_LOW);
        }
        final String validateCode = RandomUtil.randomString(PROPERTIES.getLength()).toUpperCase();
        final Map<ToolEmailConfig, JavaMailSender> mailSenderMap = MailSenderProvider.getMailSenderMap();
        Context context = new Context();
        context.setVariable(PROPERTIES.getName(), validateCode);
        String process = TEMPLATE_ENGINE.process("email", context);
        // 轮询 配置失效 继续下一个
        boolean isSend = mailSenderMap.keySet()
                                      .stream()
                                      .anyMatch(config -> {
            JavaMailSender mailSender = mailSenderMap.get(config);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            try {
                helper.setFrom(config.getFromUser());
                helper.setTo(email);
                helper.setText(process, true);
                helper.setSubject(PROPERTIES.getSubject());
                helper.setValidateAddresses(true);
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                LOG.error("send error:", e);
                return false;
            }
            return true;
        });
        if (!isSend) {
            throw new BadBusinessException(BusinessMsgState.OPT_ERROR);
        }
        // 保存验证码
        RedisUtils.set(PROPERTIES.getKeyPrefix(), email, validateCode, PROPERTIES.getExpired(), TimeUnit.MILLISECONDS);
        return null;

    }


    @Override
    public Boolean validate(String email, String code) {
        if (StringUtils.isEmpty(email) ||StringUtils.isEmpty(code)) {
            throw new BadBusinessException(BusinessMsgState.CODE_NOT_FOUNT);
        }
        final String rawCode = RedisUtils.get(PROPERTIES.getKeyPrefix(), email);
        if (!StringUtils.isEmpty(rawCode)) {
            return ObjectUtils.nullSafeEquals(rawCode, code);
        }
        return false;
    }

    @Override
    public void clear(String email) {
        RedisUtils.delete(PROPERTIES.getKeyPrefix(), email);
    }

}
