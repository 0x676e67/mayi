package com.zf1976.ant.common.component.load.aspect;


import com.power.common.util.StringUtil;
import com.zf1976.ant.common.component.load.ICache;
import com.zf1976.ant.common.component.load.annotation.CacheConfig;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.load.aspect.handler.SpringElExpressionHandler;
import com.zf1976.ant.common.component.load.enums.CacheImplement;
import com.zf1976.ant.common.component.load.impl.CaffeineCacheProvider;
import com.zf1976.ant.common.component.load.impl.RedisCacheProvider;
import com.zf1976.ant.common.component.property.CaffeineProperties;
import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WINDOWS
 */
@Aspect
@Component
public class LoadCacheAspect {

    private final Logger log = LoggerFactory.getLogger("[LoadCacheAspect]");
    private final SpringElExpressionHandler handler = new SpringElExpressionHandler();
    private Map<CacheImplement, ICache<Object, Object>> cacheProviderMap;
    public LoadCacheAspect(RedisTemplate<Object, Object> template, CaffeineProperties properties) {
        this.addProvider(CacheImplement.CAFFEINE, new CaffeineCacheProvider<>(properties));
        this.addProvider(CacheImplement.REDIS, new RedisCacheProvider<>(properties, template));
        this.checkStatus();
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CachePut) && @annotation(annotation))")
    public Object put(ProceedingJoinPoint joinPoint, CachePut annotation) {
        // 获取被代理类
        Class<?> targetClass = this.extractTargetClass(joinPoint.getThis());
        // 代理方法
        Method method = this.handler.filterMethod(joinPoint);
        // 缓存配置
        CacheConfig classAnnotation = targetClass.getAnnotation(CacheConfig.class);
        // 命名空间
        String namespace = classAnnotation == null? annotation.namespace() : classAnnotation.namespace();
        // 缓存Key
        String key = this.handler.parse(method, joinPoint.getArgs(), annotation.key(), String.class, annotation.key());
        if (annotation.dynamics()) {
            key = key.concat(SessionManagement.getCurrentUsername());
        }
        return this.cacheProviderMap.get(annotation.implement())
                                    .getValueAndSupplier(namespace, key, annotation.expired(), () -> {
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
        // 获取被代理类
        Class<?> targetClass = this.extractTargetClass(joinPoint.getThis());
        // 代理方法
        Method method = this.handler.filterMethod(joinPoint);
        // 缓存配置
        CacheConfig classAnnotation = targetClass.getAnnotation(CacheConfig.class);
        // 命名空间
        String namespace = classAnnotation == null? annotation.namespace() : classAnnotation.namespace();
        // 依赖的缓存空间
        String[] dependOnNamespace = this.concatArray(classAnnotation == null? null : classAnnotation.dependsOn(), annotation.dependsOn());
        // 缓存Key
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
            ICache<Object, Object> cacheProvider = this.cacheProviderMap.get(annotation.implement())
                    ;
            // 不存在key，清除缓存空间
            if (StringUtil.isEmpty(key)) {
                // 清除缓存空间
                cacheProvider.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cacheProvider.invalidate(depend);
                }
            } else {
                key = this.handler.parse(method, joinPoint.getArgs(), key, String.class, null);
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

    private Class<?> extractTargetClass(Object proxyObj) {
        return AopProxyUtils.ultimateTargetClass(proxyObj);
    }

    @SuppressWarnings("all")
    private String[] concatArray(String[] array1, String[] array2) {
        return ArrayUtils.addAll(array1, array2);
    }

}
