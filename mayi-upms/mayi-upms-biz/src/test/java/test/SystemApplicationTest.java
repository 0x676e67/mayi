package test;

import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.SystemApplication;
import com.zf1976.mayi.upms.biz.dao.SysPermissionDao;
import com.zf1976.mayi.upms.biz.dao.SysResourceDao;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author mac
 * @date 2021/5/17
 */
@SpringBootTest(classes = {SystemApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SystemApplicationTest {

    @Autowired
    DynamicDataSourceService dynamicDataSourceService;




}
