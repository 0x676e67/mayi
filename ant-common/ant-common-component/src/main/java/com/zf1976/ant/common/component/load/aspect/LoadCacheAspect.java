package com.zf1976.ant.common.component.load.aspect;


import com.power.common.util.StringUtil;
import com.zf1976.ant.common.component.load.ICache;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.load.aspect.handler.SpringElExpressionHandler;
import com.zf1976.ant.common.component.load.enums.CacheImplement;
import com.zf1976.ant.common.component.load.impl.CaffeineCacheProvider;
import com.zf1976.ant.common.component.load.impl.RedisCacheProvider;
import com.zf1976.ant.common.component.property.CaffeineProperties;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WINDOWS
 */
@Slf4j(topic = "[cache]")
@Aspect
@Component
public class LoadCacheAspect {

    private final SpringElExpressionHandler handler = new SpringElExpressionHandler();
    private Map<CacheImplement, ICache<Object, Object>> cacheProviderMap;
    public LoadCacheAspect(RedisTemplate<Object, Object> template, CaffeineProperties properties) {
        this.addProvider(CacheImplement.CAFFEINE, new CaffeineCacheProvider<>(properties));
        this.addProvider(CacheImplement.REDIS, new RedisCacheProvider<>(properties, template));
        this.checkStatus();
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CachePut) && @annotation(annotation))")
    public Object put(ProceedingJoinPoint joinPoint, CachePut annotation) {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String namespace = annotation.namespace();
        String key = this.handler.parse(method, joinPointArgs, annotation.key(), String.class, annotation.key());
        if (annotation.dynamics()) {
            key = key.concat(getUsername());
        }
        ICache<Object, Object> cacheProvider = this.getProvider(annotation.implement());
        return cacheProvider.getValueAndSupplier(namespace, key, annotation.expired(), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                if (log.isWarnEnabled()) {
                    log.warn(throwable.getMessage(), throwable);
                }
                return null;
            }
        });
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CacheEvict) && @annotation(annotation)")
    public Object remove(ProceedingJoinPoint joinPoint, CacheEvict annotation) throws Throwable {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String[] dependOnNamespace = annotation.dependsOn();
        String namespace = annotation.namespace();
        String key = annotation.key();
        // 默认策略清除所有缓存实现的命名空间
        if (annotation.strategy()) {
            this.cacheProviderMap.forEach((relation, cache) -> {
                // 清除缓存空间
                cache.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cache.invalidate(depend);
                }
            });
        } else {
            // 根据缓存实现清除命名空间
            ICache<Object, Object> cacheProvider = this.getProvider(annotation.implement());
            // 不存在key，清除缓存空间
            if (StringUtil.isEmpty(key)) {
                // 清除缓存空间
                cacheProvider.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cacheProvider.invalidate(depend);
                }
            } else {
                key = this.handler.parse(method, joinPointArgs, key, String.class, null);
                cacheProvider.invalidate(namespace, key);
            }
        }
        return joinPoint.proceed();
    }

    private void checkStatus() {
        Assert.notNull(this.handler, "expression handler Uninitialized!");
        Assert.notNull(this.cacheProviderMap, "cache provider Uninitialized!");
    }

    public void addProvider(CacheImplement relation, ICache<Object, Object> cacheProvider) {
        if (this.cacheProviderMap == null) {
            this.cacheProviderMap = new ConcurrentHashMap<>(2);
        }
        this.cacheProviderMap.put(relation, cacheProvider);
    }

    public ICache<Object, Object> getProvider(CacheImplement implement) {
        return this.cacheProviderMap.get(implement);
    }

    private String getUsername(){
        return SessionManagement.getUsername();
    }
}
