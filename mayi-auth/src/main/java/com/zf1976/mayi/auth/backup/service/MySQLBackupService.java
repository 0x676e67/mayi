package com.zf1976.mayi.auth.backup.service;

import com.zf1976.mayi.auth.backup.MySQLStrategyBackup;
import com.zf1976.mayi.auth.backup.SQLBackupStrategy;
import com.zf1976.mayi.auth.backup.property.SQLBackupProperties;
import com.zf1976.mayi.auth.exception.SQLBackupException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author ant
 * Create by Ant on 2021/6/8 4:53 上午
 */
@Service(value = "MySQLBackupService")
public class MySQLBackupService {

    private final SQLBackupStrategy sqlBackupStrategy;
    private final SQLBackupProperties properties;

    public MySQLBackupService(DataSource dataSource, SQLBackupProperties properties) {
        this.sqlBackupStrategy = new MySQLStrategyBackup(dataSource);
        this.properties = properties;
    }

    /**
     * 创建备份
     *
     * @return {@link Void}
     */
    public Void createBackup() {
        if (!this.sqlBackupStrategy.backup(properties.getHome(), properties.getDirectory())) {
            throw new SQLBackupException("Failed to create backup file");
        }
        return null;
    }

}
