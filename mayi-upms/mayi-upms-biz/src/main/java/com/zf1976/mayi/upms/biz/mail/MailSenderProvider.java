package com.zf1976.mayi.upms.biz.mail;

import com.zf1976.mayi.common.core.util.SpringContextHolder;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.upms.biz.mail.dao.ToolEmailConfigDao;
import com.zf1976.mayi.upms.biz.mail.pojo.ToolEmailConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author mac
 */
public class MailSenderProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSenderProvider.class);

    private volatile static Map<ToolEmailConfig, JavaMailSender> mailSenderMap;

    public static  Map<ToolEmailConfig, JavaMailSender> getMailSenderMap() {
        if (mailSenderMap == null) {
            synchronized (MailSenderProvider.class) {
                if (mailSenderMap == null) {
                    init(SpringContextHolder.getBean(MailProperties.class));
                }
            }
        }
        return mailSenderMap;
    }

    public static void init(MailProperties properties) {
        HashMap<ToolEmailConfig, JavaMailSender> mailSenderHashMap = new HashMap<>(1);
        ToolEmailConfigDao emailConfigDao = SpringContextHolder.getBean(ToolEmailConfigDao.class);
        emailConfigDao.selectList(null).forEach(config -> {
            final JavaMailSenderImpl var1 = new JavaMailSenderImpl();
            applyProperties(properties,var1);
            try {
                if (config.getPort() != null) {
                    var1.setPort(config.getPort());
                }
                if (config.getHost() != null) {
                    var1.setHost(config.getHost());
                }
                if (config.getUser() != null) {
                    var1.setUsername(config.getUser());
                }
                if (config.getPass() != null) {
                    var1.setPassword(EncryptUtil.decryptForRsaByPrivateKey(config.getPass()));
                }
            } catch (Exception e) {
                LOGGER.error("decode error:", e);
            }
            mailSenderHashMap.put(config,var1);
        });
        final JavaMailSenderImpl var2 = new JavaMailSenderImpl();
        applyProperties(properties, var2);
        ToolEmailConfig defaultConfig = new ToolEmailConfig();
        defaultConfig.setFromUser(var2.getUsername());
        defaultConfig.setUser(var2.getUsername());
        defaultConfig.setPass(var2.getPassword());
        defaultConfig.setHost(var2.getHost());
        defaultConfig.setPort(var2.getPort());
        mailSenderHashMap.put(defaultConfig, var2);
        MailSenderProvider.mailSenderMap = mailSenderHashMap;
    }

    private static void applyProperties(MailProperties properties, JavaMailSenderImpl sender) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }

        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }

        if (!properties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(properties.getProperties()));
        }

    }

    private static Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }
}
