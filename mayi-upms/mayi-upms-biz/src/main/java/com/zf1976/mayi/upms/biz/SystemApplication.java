package com.zf1976.mayi.upms.biz;

import com.zf1976.mayi.upms.biz.feign.SecurityClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author mac
 * @date 2021/1/14
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@MapperScan(value = "com.zf1976", annotationClass = Repository.class)
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients(basePackages = "com.zf1976")
public class SystemApplication {

    public static void main(String[] args) {
        disableWarning();
        SpringApplication.run(SystemApplication.class, args);
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
