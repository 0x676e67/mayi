package test;

import com.zf1976.mayi.auth.AuthApplication;
import com.zf1976.mayi.upms.biz.security.backup.service.MySQLBackupService;
import com.zf1976.mayi.upms.biz.dao.SysPermissionDao;
import com.zf1976.mayi.auth.service.DynamicDataSourceService;
import com.zf1976.mayi.auth.service.OAuth2ClientService;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.upms.biz.dao.SysRoleDao;
import com.zf1976.mayi.upms.biz.dao.SysUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;


/**
 * @author mac
 * @date 2021/2/5
 **/
@SpringBootTest(classes = {AuthApplication.class})
public class AuthApplicationTest {

    @Autowired
    private OAuth2ClientService clientService;

    @Autowired
    SysUserDao userDao;

    @Autowired
    SysRoleDao roleDao;


    @Autowired
    DataSource dataSource;

    @Autowired
    DynamicDataSourceService dynamicDataSourceService;

    @Autowired
    private SysPermissionDao permissionDao;

    @Autowired
    private MySQLBackupService mySQLBackupService;

    @Test
    public void sqlBackupTest() throws InterruptedException {
        this.mySQLBackupService.deleteBackupFileByFilename("master-backup_2021-06-08_22-05-48_1623161148428.sql");
    }

    @Test
    public void sessionTest() {
        try {
            System.out.println(EncryptUtil.encryptForRsaByPublicKey("123456"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
