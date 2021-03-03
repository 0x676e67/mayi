package com.zf1976.ant.common.security.cache.validate.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.power.common.util.StringUtil;
import com.zf1976.ant.common.security.cache.CacheCreationPolicy;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import com.zf1976.ant.common.security.cache.validate.repository.CaptchaRepositoryStrategy;
import com.zf1976.ant.common.security.cache.validate.repository.impl.CaptchaLocalRepositoryStrategy;
import com.zf1976.ant.common.security.cache.validate.repository.impl.CaptchaRedisRepositoryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 提醒这里有个Mybatis Plus 跟springboot的坑
 * Application Context 上下文为初始化完前
 *
 * @author mac
 * Create by Ant on 2020/9/1 下午2:10
 */
@Service
public class CaptchaServiceImpl implements CaptchaService, SmartLifecycle {

    private static final Log LOG = LogFactory.getLog(CaptchaServiceImpl.class);
    private static final AtomicReference<CaptchaRepositoryStrategy> STRATEGY_ATOMIC_REFERENCE = new AtomicReference<>();
    private static final AtomicBoolean IS_LOCAL = new AtomicBoolean(false);
    private static final AtomicBoolean IS_EXECUTE = new AtomicBoolean(false);
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition CONDITION = LOCK.newCondition();
    private static volatile boolean isRunning = false;
    private CaptchaRepositoryStrategy localRepositoryStrategy;
    private CaptchaRepositoryStrategy redisRepositoryStrategy;
    private ScheduledExecutorService executorService;

    @Override
    public boolean sendCaptcha(String key, String value, Long expire, TimeUnit timeUnit) {
        this.loadAwait();
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
            return false;
        }
        final byte maxSwitch = 2;
        for (int i = 0; i < maxSwitch; i++) {
            CaptchaRepositoryStrategy strategy = STRATEGY_ATOMIC_REFERENCE.get();
            if (strategy.save(key, value, expire, timeUnit)) {
                return true;
            } else {
                this.choiceStrategy();
            }
        }
        return false;
    }

    @Override
    public boolean validateCaptcha(String key, String code) {
        this.loadAwait();
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(code)) {
            return false;
        }
        Optional<CaptchaRepositoryStrategy> optional = Optional.ofNullable(STRATEGY_ATOMIC_REFERENCE.get());
        if (optional.isPresent()) {
            CaptchaRepositoryStrategy strategy = optional.get();
            final String awaitCode = strategy.get(key);
            if (!StringUtil.isEmpty(awaitCode)) {
                boolean validate = awaitCode.equalsIgnoreCase(code);
                if (validate) {
                    this.clearCaptcha(key);
                }
                return validate;
            }
        }
        return false;
    }

    @Override
    public void clearCaptcha(String key) {
        Assert.notNull(STRATEGY_ATOMIC_REFERENCE.get(), "repository cannot be null");
        STRATEGY_ATOMIC_REFERENCE.get().invalidate(key);
    }

    private void loadAwait() {
        while (true) {
            if (IS_EXECUTE.get()) {
                LOCK.lock();
                try {
                    LOG.info("load await lock");
                } finally {
                    LOCK.unlock();
                }
            } else {
                break;
            }
        }
    }

    private void choiceStrategy() {
        if (IS_LOCAL.get()) {
            this.setCachePolicy(CacheCreationPolicy.LOCAL);
            IS_LOCAL.compareAndSet(true, false);
        } else {
            this.setCachePolicy(CacheCreationPolicy.REDIS);
            IS_LOCAL.compareAndSet(false, true);
        }
    }

    private void setCachePolicy(CacheCreationPolicy cachePolicy) {
        Assert.notNull(cachePolicy, "cache policy enum cannot be null");
        switch (cachePolicy) {
            case LOCAL:
                STRATEGY_ATOMIC_REFERENCE.compareAndSet(this.localRepositoryStrategy, this.redisRepositoryStrategy);
                break;
            case REDIS:
                STRATEGY_ATOMIC_REFERENCE.compareAndSet(this.redisRepositoryStrategy, this.localRepositoryStrategy);
                break;
            default:
        }
    }

    private static CaptchaRepositoryStrategy createLocalRepositoryStrategy() {
        return CaptchaLocalRepositoryStrategy.getInstance();
    }

    private static CaptchaRepositoryStrategy createRedisRepositoryStrategy() {
        return CaptchaRedisRepositoryStrategy.getInstance();
    }

    private void executePolicyTask() {
        ChoicePolicyTask choicePolicyTask = new ChoicePolicyTask(this.localRepositoryStrategy,
                                                                 this.redisRepositoryStrategy);
        this.executorService.scheduleWithFixedDelay(choicePolicyTask,
                                                    10,
                                                    10,
                                                    TimeUnit.MINUTES);


    }

    /**
     * 初始化完所有bean
     * 容器启动调用
     */
    @Override
    public void start() {
        this.localRepositoryStrategy = createLocalRepositoryStrategy();
        this.redisRepositoryStrategy = createRedisRepositoryStrategy();
        this.executorService = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setNameFormat("cache choice strategy thread").build());
        this.executePolicyTask();
        STRATEGY_ATOMIC_REFERENCE.set(this.redisRepositoryStrategy);
        isRunning = true;
    }

    /**
     * 容器停止调用
     */
    @Override
    public void stop() {
        if (!this.executorService.isShutdown()) {
            this.executorService.shutdown();
        }
        isRunning = false;
    }

    /**
     * 检查组件是否在运行
     *
     * @return false 执行start() true 执行stop()
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public static class ChoicePolicyTask implements Runnable {

        private final CaptchaRepositoryStrategy localStrategy;
        private final CaptchaRepositoryStrategy redisStrategy;
        private final CaptchaProperties config;

        public ChoicePolicyTask(CaptchaRepositoryStrategy rawStrategy, CaptchaRepositoryStrategy newStrategy) {
            this.localStrategy = rawStrategy;
            this.redisStrategy = newStrategy;
            this.config = ApplicationConfigUtils.getCaptchaProperties();
        }

        @Override
        public void run() {
            LOG.info(Thread.currentThread().getState());
            if (IS_LOCAL.get()) {
                IS_EXECUTE.compareAndSet(false, true);
                boolean isConnect = false;
                if (this.redisStrategy.isAvailable()) {
                    for (Map.Entry<String, String> entry : this.localStrategy.asMap().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (this.redisStrategy.save(key, value, this.config.getExpiration(), TimeUnit.MILLISECONDS)) {
                            isConnect = true;
                        } else {
                            LOG.error("Unable to connect redis");
                            isConnect = false;
                            break;
                        }
                    }
                }
                if (isConnect) {
                    final boolean choicePolicy = STRATEGY_ATOMIC_REFERENCE.compareAndSet(this.localStrategy, this.redisStrategy);
                    final boolean updatePolicy = IS_LOCAL.compareAndSet(true, false);
                    this.localStrategy.invalidateAll();
                    LOG.info("choice policy：" + (choicePolicy && updatePolicy));
                }
            }
            IS_EXECUTE.compareAndSet(true, false);
        }
    }

}
