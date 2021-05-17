package test;

import com.zf1976.ant.auth.AuthApplication;
import com.zf1976.ant.auth.serialize.JacksonSerializationStrategy;
import com.zf1976.ant.auth.service.impl.OAuth2ClientService;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.auth.system.MySqlStrategyBackup;
import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;


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
    ResourceService service;

    @Autowired
    DataSource dataSource;

    @Value("${sql-backup.path}")
    String path;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void serializationTest() {
        JacksonSerializationStrategy jacksonSerializationStrategy = new JacksonSerializationStrategy();
        byte[] bytes = Objects.requireNonNull(this.redisTemplate.getConnectionFactory())
                              .getConnection()
                              .get("id_to_session:2".getBytes(StandardCharsets.UTF_8));
        Session session = jacksonSerializationStrategy.deserialize(bytes, Session.class);
        System.out.println(session);
    }

    @Test
    public void backupTest() {
        final MySqlStrategyBackup strategyBackupHolder = new MySqlStrategyBackup(this.dataSource);
        final boolean backup = strategyBackupHolder.backup(System.getProperty("user.home"), this.path);
        if (backup) {
            System.out.println("备份成功");
        }
        if (strategyBackupHolder.recover("/Users/mac/work/backup/2021-05-14-21-32-46.sql")) {
            System.out.println("恢复成功");
        }
    }
}
