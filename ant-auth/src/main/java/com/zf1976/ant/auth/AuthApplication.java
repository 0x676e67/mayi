package com.zf1976.ant.auth;

import com.zf1976.ant.common.encrypt.EncryptUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Repository;

/**
 * @author mac
 * @date 2021/2/10
 **/
@SpringBootApplication(scanBasePackages = "com.zf1976")
@MapperScan(value = "com.zf1976", annotationClass = Repository.class)
@EnableDiscoveryClient
public class AuthApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AuthApplication.class, args);
        final String encrypt = EncryptUtil.encryptForRsaByPublicKey("123456");
        final String decrypt = EncryptUtil.decryptForRsaByPrivateKey(encrypt);
        System.out.println(encrypt);
        System.out.println(decrypt);
    }

}
