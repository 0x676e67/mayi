package com.zf1976.ant.test;

import com.zf1976.ant.common.security.support.session.RedisSessionHolder;
import com.zf1976.ant.upms.biz.SystemApplication;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ant
 * Create by Ant on 2021/2/3 8:19 PM
 */
@SpringBootTest(classes = SystemApplication.class)
public class SystemApplicationTest {

    @Test
    public void contextLoads() {
        var session = RedisSessionHolder.readSession(1L);
        System.out.println(session.getToken());
    }
}
