package test;

import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
import com.zf1976.ant.upms.biz.SystemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author mac
 * @date 2021/5/17
 */
@SpringBootTest(classes = {SystemApplication.class})
public class SystemApplicationTest {

    @Test
    public void sessionTest() {
        SessionManagement.removeSession(1L);
    }
}
