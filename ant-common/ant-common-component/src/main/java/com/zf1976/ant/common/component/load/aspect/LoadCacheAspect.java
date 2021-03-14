package com.zf1976.ant.common.component.load.aspect;


import com.power.common.util.StringUtil;
import com.zf1976.ant.common.component.load.impl.CaffeineCacheProvider;
import com.zf1976.ant.common.component.load.ICache;
import com.zf1976.ant.common.component.load.impl.RedisCacheProvider;
import com.zf1976.ant.common.component.load.annotation.CaffeineEvict;
import com.zf1976.ant.common.component.load.annotation.CaffeinePut;
import com.zf1976.ant.common.component.load.aspect.handler.SpringElExpressionHandler;
import com.zf1976.ant.common.component.load.enums.CacheRelation;
import com.zf1976.ant.common.core.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WINDOWS
 */
@Slf4j(topic = "[cache]")
@Aspect
@Component
@SuppressWarnings("rawtypes")
public class LoadCacheAspect {

    private final SpringElExpressionHandler handler;
    private Map<CacheRelation,ICache<Object, Object>> cacheProviderMap;
    public LoadCacheAspect(RedisTemplate<String, Map> mapRedisTemplate) {
        cacheProviderMap = new ConcurrentHashMap<>();
        cacheProviderMap.put(CacheRelation.CAFFEINE, new CaffeineCacheProvider<>());
        cacheProviderMap.put(CacheRelation.REDIS, new RedisCacheProvider<>(mapRedisTemplate));
        handler = new SpringElExpressionHandler();
        this.checkStatus();
    }

    private void checkStatus() {
        Assert.notNull(this.handler, "expression handler Uninitialized!");
        Assert.notNull(this.cacheProviderMap, "cache provider Uninitialized!");
    }

    public void setCacheProvider(CacheRelation relation, ICache<Object, Object> cacheProvider) {
        if (this.cacheProviderMap == null) {
            this.cacheProviderMap = new ConcurrentHashMap<>(2);
        }
        this.cacheProviderMap.put(relation, cacheProvider);
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CaffeinePut) && @annotation(annotation))")
    public Object saveCache(ProceedingJoinPoint joinPoint, CaffeinePut annotation) {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String  key = this.handler.parse(method, joinPointArgs, annotation.key(), String.class, annotation.key());
        if (annotation.dynamicsKey()) {
            key = this.formatKey(this.getPrincipal(), key);
        }
        ICache<Object, Object> cacheProvider = this.getCacheProvider(annotation.relation());
        return cacheProvider.get(annotation.namespace(), key, annotation.expired(), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                if (log.isWarnEnabled()) {
                    log.warn(throwable.getMessage(), throwable);
                }
                return Optional.empty();
            }
        });
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CaffeineEvict) && @annotation(annotation)")
    public Object removeCache(ProceedingJoinPoint joinPoint, CaffeineEvict annotation) throws Throwable {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String namespace = annotation.namespace();
        String[] dependOnNamespace = annotation.dependsOn();
        ICache<Object, Object> cacheProvider = this.getCacheProvider(annotation.relation());
        if (StringUtil.isEmpty(annotation.key())) {
            Arrays.stream(dependOnNamespace)
                  .forEachOrdered(cacheProvider::invalidate);
            cacheProvider.invalidate(namespace);
        } else {
            String key = this.handler.parse(method, joinPointArgs, annotation.key(), String.class, null);
            cacheProvider.invalidate(namespace, key);
        }
        return joinPoint.proceed();
    }

    private ICache<Object, Object> getCacheProvider(CacheRelation relation) {
        return this.cacheProviderMap.get(relation);
    }

    private String formatKey(Object prefix, String key) {
        return prefix + "-" + key;
    }


    private String getPrincipal() {
        return RequestUtils.getAuthentication();
    }

}
