package com.zf1976.mayi.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author mac
 * @date 2021/2/10
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients(basePackageClasses = RemoteUserService.class)
@EnableConfigurationProperties
public class AuthApplication {

    public static void main(String[] args) {
        disableWarning();
        SpringApplication.run(AuthApplication.class, args);
    }

    /**
     * 关闭runtime warnings
     */
    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            unsafe.putObjectVolatile(cls, unsafe.staticFieldOffset(logger), null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
