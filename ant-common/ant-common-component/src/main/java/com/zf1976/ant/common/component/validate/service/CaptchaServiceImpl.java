package com.zf1976.ant.common.component.validate.service;

import com.zf1976.ant.common.component.validate.repository.CaptchaRepository;
import com.zf1976.ant.common.component.validate.repository.impl.RedisCaptchaRepository;
import com.zf1976.ant.common.component.property.CaptchaProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * 提醒这里有个Mybatis Plus 跟springboot的坑
 * Application Context 上下文为初始化完前
 *
 * @author mac
 * Create by Ant on 2020/9/1 下午2:10
 */
@Service("captchaService")
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaProperties properties;
    private final CaptchaRepository repository;

    public CaptchaServiceImpl(CaptchaProperties properties, StringRedisTemplate stringRedisTemplate) {
        this.properties = properties;
        this.repository = new RedisCaptchaRepository(stringRedisTemplate);
    }

    @Override
    public boolean storeCaptcha(String key, String value) {
        if (this.repository.isAvailable()) {
            try {
                repository.store(this.keyFormatter(key), value, properties.getExpiration(), TimeUnit.MINUTES);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean validateCaptcha(String key, String code) {
        if (this.repository.isAvailable()) {
            final String storeCode = repository.read(this.keyFormatter(key));
            this.repository.remove(this.keyFormatter(key));
            return ObjectUtils.nullSafeEquals(storeCode, code);
        }
        return false;
    }

    @Override
    public void clearCaptcha(String key) {
        this.repository.remove(this.keyFormatter(key));
    }

    private String keyFormatter(String key) {
        return this.properties.getKeyPrefix() + key;
    }

}
