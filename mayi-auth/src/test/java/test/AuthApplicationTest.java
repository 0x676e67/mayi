package test;

import com.zf1976.mayi.auth.AuthApplication;
import com.zf1976.mayi.auth.backup.SQLBackupStrategy;
import com.zf1976.mayi.auth.backup.service.MySQLBackupService;
import com.zf1976.mayi.auth.dao.SysPermissionDao;
import com.zf1976.mayi.auth.pojo.BackupFile;
import com.zf1976.mayi.auth.pojo.RoleBinding;
import com.zf1976.mayi.auth.service.DynamicDataSourceService;
import com.zf1976.mayi.auth.service.OAuth2ClientService;
import com.zf1976.mayi.auth.backup.MySQLStrategyBackup;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.upms.biz.dao.SysRoleDao;
import com.zf1976.mayi.upms.biz.dao.SysUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.TimeUnit;


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
        List<String> dateList = this.mySQLBackupService.selectBackupDate();
        List<BackupFile> backupFiles = this.mySQLBackupService.selectBackupFileByDate(dateList.get(0), 1);
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
