package com.zf1976.ant.common.component.load.aspect;


import com.power.common.util.StringUtil;
import com.zf1976.ant.common.component.load.ICache;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.load.aspect.handler.SpringElExpressionHandler;
import com.zf1976.ant.common.component.load.enums.CacheRelation;
import com.zf1976.ant.common.component.load.impl.CaffeineCacheProvider;
import com.zf1976.ant.common.component.load.impl.RedisCacheProvider;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.component.property.CacheProperties;
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

    private final SpringElExpressionHandler handler;
    private Map<CacheRelation, ICache<Object, Object>> cacheProviderMap;
    public LoadCacheAspect(RedisTemplate<Object, Map<Object, Object>> mapRedisTemplate, CacheProperties properties) {
        cacheProviderMap = new ConcurrentHashMap<>();
        cacheProviderMap.put(CacheRelation.CAFFEINE, new CaffeineCacheProvider<>(properties));
        cacheProviderMap.put(CacheRelation.REDIS, new RedisCacheProvider<>(properties, mapRedisTemplate));
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

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CachePut) && @annotation(cachePut))")
    public Object saveCache(ProceedingJoinPoint joinPoint, CachePut cachePut) {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String key = this.handler.parse(method, joinPointArgs, cachePut.key(), String.class, cachePut.key());
        if (cachePut.dynamicsKey()) {
            key = this.formatDynamicsKey(this.getPrincipal(), key);
        }
        ICache<Object, Object> cacheProvider = this.getCacheProvider(cachePut.relation());
        return cacheProvider.get(cachePut.namespace(), key, cachePut.expired(), () -> {
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

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CacheEvict) && @annotation(cacheEvict)")
    public Object removeCache(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) throws Throwable {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String[] dependOnNamespace = cacheEvict.dependsOn();
        String namespace = cacheEvict.namespace();
        // 默认策略清除所有缓存实现的命名空间
        if (cacheEvict.relation() == CacheRelation.DEFAULT) {
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
            ICache<Object, Object> cacheProvider = this.getCacheProvider(cacheEvict.relation());
            // 不存在key，清除缓存空间
            if (StringUtil.isEmpty(cacheEvict.key())) {
                // 清除缓存空间
                cacheProvider.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cacheProvider.invalidate(depend);
                }
            } else {
                String key = this.handler.parse(method, joinPointArgs, cacheEvict.key(), String.class, null);
                cacheProvider.invalidate(namespace, key);
            }
        }
        return joinPoint.proceed();
    }

    private ICache<Object, Object> getCacheProvider(CacheRelation relation) {
        return this.cacheProviderMap.get(relation);
    }

    private String formatDynamicsKey(Object prefix, String key) {
        return prefix + "-" + key;
    }


    private String getPrincipal() {
        return RequestUtils.getAuthentication();
    }

}
