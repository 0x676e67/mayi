package com.zf1976.mayi.auth.backup.service;

import com.zf1976.mayi.auth.backup.MySQLStrategyBackup;
import com.zf1976.mayi.auth.backup.SQLBackupStrategy;
import com.zf1976.mayi.auth.backup.property.SQLBackupProperties;
import com.zf1976.mayi.auth.exception.SQLBackupException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ant
 * Create by Ant on 2021/6/8 4:53 上午
 */
@Service(value = "mySQLBackupService")
public class MySQLBackupService {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Pattern pattern = Pattern.compile("[0-9]");
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
        // 备份文件目录
        var backupFileDirectory = this.getBackupParentFileDirectory();
        // 根据目录存在备份子目录过滤
        var dateDirectoryList = backupFileDirectory.listFiles(pathname -> pathname.isDirectory() && pathname.exists());
        // 按时间划分目录不存在
        if (dateDirectoryList != null) {
            // 按备份日期划分
            var backupDateFileDirectory = this.createBackupDateFileDirectory();
            // 子目录文件列表
            var childFileDirectoryList = backupDateFileDirectory.listFiles(pathname -> {
                int count = 0;
                // 匹配到两个数字命名侧过滤
                int flag = 2;
                while (this.pattern.matcher(pathname.getName()).find()) {
                    if ((++count) == 2) {
                        return false;
                    }
                }
                return true;
            });

            if (childFileDirectoryList != null) {
                for (File childrenFileDirectory : childFileDirectoryList) {

                }
            } else {
                // 按序号创建子目录
                for (int i = 0; i <= this.properties.getDayTotal(); i++) {
                    var childFileDirectory = this.createBackupChildFileDirectory(getDateDirectoryName(), i);
                    // 创建备份文件成功退出
                    if (this.sqlBackupStrategy.backup(childFileDirectory)) {
                        break;
                    }
                }
            }
        } else {
            // 按备份日期划分、按序号创建子目录
            for (int i = 0; i <= this.properties.getDayTotal(); i++) {
                var childFileDirectory = this.createBackupChildFileDirectory(getDateDirectoryName(), i);
                // 创建备份文件成功退出
                if (this.sqlBackupStrategy.backup(childFileDirectory)) {
                    break;
                }
            }

        }
        return null;
    }

    public void selectBackupFile(){
        var backupFileDirectory = this.getBackupParentFileDirectory();
        var backupFileList = backupFileDirectory.listFiles();
        if (backupFileList != null) {
            for (File backupFile : backupFileList) {
            }
        }
    }



    private File getBackupParentFileDirectory() {
        var backupFileDirectory = Paths.get(this.properties.getHome(), this.properties.getDirectory())
                        .toFile();
        if (!backupFileDirectory.isDirectory()) {
            throw new SQLBackupException("Not a directory");
        }
        return backupFileDirectory;
    }

    private File createBackupDateFileDirectory() {
        var parentFileDirectory = this.getBackupParentFileDirectory();
        var path = Paths.get(parentFileDirectory.getAbsolutePath(), this.getDateDirectoryName());
        try {
            return Files.createFile(path).toFile();
        } catch (IOException e) {
            throw new SQLBackupException("创建备份目录失败", e.getCause());
        }
    }

    private File createBackupChildFileDirectory(String parent, int index) {
        var path = Paths.get(parent, String.valueOf(index));
        try {
            return Files.createDirectory(path)
                        .toFile();
        } catch (IOException e) {
            throw new SQLBackupException("创建备份目录失败", e.getCause());
        }
    }

    private String getDateDirectoryName(){
        synchronized (this) {
            return DATE_FORMAT.format(new Date());
        }
    }

    public static void main(String[] args) {
        var file = Paths.get("/Users/ant", "/.mayi/backup")
                        .toFile();
        var files = file.listFiles((pathname -> pathname.isDirectory() && pathname.exists()));
        assert files != null;
        for (File file1 : files) {
            System.out.println(file1.getName());
        }
        var matcher = Pattern.compile("[0-9]").matcher("123");

    }
}
