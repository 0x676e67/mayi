package com.zf1976.ant.test;

import com.zf1976.ant.upms.biz.SystemApplication;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ant
 * Create by Ant on 2021/2/3 8:19 PM
 */
@SpringBootTest(classes = {SystemApplication.class})
public class SystemApplicationTest {

    @Autowired
    private SysRoleDao sysRoleDao;

    @Test
    public void contextLoads() {
        System.out.println(sysRoleDao);
    }
}
