package com.zf1976.mayi.auth.backup.service;

import com.zf1976.mayi.auth.backup.MySQLStrategyBackup;
import com.zf1976.mayi.auth.backup.SQLBackupStrategy;
import com.zf1976.mayi.auth.backup.property.SQLBackupProperties;
import com.zf1976.mayi.auth.exception.SQLBackupException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final int pageSize;

    public MySQLBackupService(DataSource dataSource, SQLBackupProperties properties) {
        this.sqlBackupStrategy = new MySQLStrategyBackup(dataSource);
        this.properties = properties;
        this.pageSize = properties.getDayTotal() / properties.getFileCountSize();
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
        var dateDirectoryArray = backupFileDirectory.listFiles(pathname -> pathname.isDirectory() && pathname.exists());
        boolean condition = false;
        // 按日期划分目录不存在
        if (dateDirectoryArray != null && dateDirectoryArray.length > 0) {
            // 按备份日期划分
            var dateFileDirectory = this.getBackupDateFileDirectory();
            // 过滤子目录文件列表
            var childFileDirectoryArray = this.getChildFileAndFilter(dateFileDirectory);
            // 存在按0-9序号划分目录
            boolean createNewIndexDirectory = false;
            if (childFileDirectoryArray != null && childFileDirectoryArray.length > 0) {
                // 按0-9序号划分
                for (File childFileDirectory : childFileDirectoryArray) {
                    // 重置标记
                    createNewIndexDirectory = false;
                    File[] backupFileArray = childFileDirectory.listFiles();
                    // 当前目录存在备份文件
                    if (backupFileArray != null) {
                        List<File> backupFileList = Arrays.stream(backupFileArray)
                                                          .filter(file -> !file.isHidden() && file.getName()
                                                                                                  .startsWith(this.sqlBackupStrategy.getDatabase()))
                                                          .collect(Collectors.toList());
                        // 当目录备份文件数小于限定
                        if (backupFileList.size() < this.properties.getFileCountSize()) {
                            // 创建备份文件成功退出
                            if (this.sqlBackupStrategy.backup(childFileDirectory)) {
                                break;
                            }
                            throw new SQLBackupException("Failed to backup database");
                        } else {
                            // 新增目录上限判断
                            if ((childFileDirectoryArray.length + 1) <= this.pageSize) {
                                createNewIndexDirectory = true;
                            } else {
                                throw new SQLBackupException("Maximum number of backup files created that day");
                            }
                        }
                    }
                }
                // 创建新目录并备份
                if (createNewIndexDirectory) {
                    File backupChildFileDirectory = this.getBackupChildFileDirectory(childFileDirectoryArray.length);
                    if (this.sqlBackupStrategy.backup(backupChildFileDirectory)) {
                        return null;
                    }
                }
            } else {
                condition = true;
            }
        } else {
            condition = true;
        }
        if (condition) {
            // 按0-9序号划分目录并创建文件
            for (int i = 0; i <= this.pageSize; i++) {
                var childFileDirectory = this.getBackupChildFileDirectory(i);
                // 创建备份文件成功退出
                if (!this.sqlBackupStrategy.backup(childFileDirectory)) {
                    throw new SQLBackupException("Failed to backup database");
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


    private File[] getChildFileAndFilter(File dateFileDirectory) {
        return dateFileDirectory.listFiles(pathname -> {
            // 按数字序号过滤目录文件名
            for (int i = 0; i < this.pageSize; i++) {
                if (String.valueOf(i)
                          .equals(pathname.getName())) {
                    return true;
                }
            }
            return false;
        });
    }

    private File getBackupParentFileDirectory() {
        return Paths.get(this.properties.getHome(), this.properties.getDirectory())
                    .toFile();
    }

    private File getBackupDateFileDirectory() {
        var parentFileDirectory = this.getBackupParentFileDirectory();
        return Paths.get(parentFileDirectory.getAbsolutePath(), this.getDateDirectoryName())
                    .toFile();
    }

    private File getBackupChildFileDirectory(String parent, int index) {
        return Paths.get(parent, String.valueOf(index)).toFile();
    }

    private File getBackupChildFileDirectory(int index) {
        File backupDateFileDirectory = this.getBackupDateFileDirectory();
        return this.getBackupChildFileDirectory(backupDateFileDirectory.getAbsolutePath(), index);
    }

    private String getDateDirectoryName(){
        synchronized (this) {
            return DATE_FORMAT.format(new Date());
        }
    }

    public static void main(String[] args) {
        var file = Paths.get("/Users/mac", "/.mayi/backup")
                        .toFile();
        var files = file.listFiles((pathname -> pathname.isDirectory() && pathname.exists()));
        assert files != null;

    }
}
