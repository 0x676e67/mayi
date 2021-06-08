package com.zf1976.mayi.auth.backup.service;

import com.power.common.util.PrettyMemoryUtil;
import com.zf1976.mayi.auth.backup.MySQLStrategyBackup;
import com.zf1976.mayi.auth.backup.SQLBackupStrategy;
import com.zf1976.mayi.auth.backup.property.SQLBackupProperties;
import com.zf1976.mayi.auth.exception.SQLBackupException;
import com.zf1976.mayi.auth.pojo.BackupFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private final int pageCount;

    public MySQLBackupService(DataSource dataSource, SQLBackupProperties properties) {
        this.sqlBackupStrategy = new MySQLStrategyBackup(dataSource);
        this.properties = properties;
        this.pageCount = properties.getDayTotal() / properties.getFileCountSize();
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
        var dateDirectoryArray = this.getArrayFilterHiddenAndDirectory(backupFileDirectory);

        // 按日期划分目录不存在
        if (dateDirectoryArray == null) {
            this.createBackupFileByDefault();
            return null;
        }

        // 按备份日期划分
        var dateFileDirectory = this.getBackupDateFileDirectory();
        // 过滤子目录文件列表
        var childFileDirectoryArray = this.getChildFileAndFilter(dateFileDirectory);
        // 存在按0-9序号划分目录
        boolean createNewIndexDirectory = false;
        if (childFileDirectoryArray == null) {
           this.createBackupFileByDefault();
        } else {
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
                    } else {
                        // 新增目录上限判断
                        if ((childFileDirectoryArray.length + 1) <= this.pageCount) {
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
                this.createBackupFile(backupChildFileDirectory);
            }
        }
        return null;
    }

    /**
     * 按字符串日期进行分页查询备份文件
     *
     * @param date 自定义字符串日期格式
     * @return {@link List<BackupFile>}
     */
    public List<BackupFile> selectBackupFileByDate(String date, Integer page){
        if (StringUtils.isEmpty(date)) {
            return Collections.emptyList();
        }
        File backupParentFileDirectory = this.getBackupParentFileDirectory();
        File[] dateFileDirectoryArray = this.getArrayFilterHiddenAndDirectory(backupParentFileDirectory);
        if (dateFileDirectoryArray != null) {
            File targetFileDirectory = null;
            for (File dateFileDirectory : dateFileDirectoryArray) {
                if (dateFileDirectory.getName().equals(date)) {
                    targetFileDirectory = dateFileDirectory;
                    break;
                }
            }
            if (targetFileDirectory != null) {
                if (page != null && page > 0 && page <= this.pageCount) {
                    File[] childFileDirectory = this.getArrayFilterHiddenAndDirectory(targetFileDirectory);
                    if (childFileDirectory != null) {
                        File[] targetFileArray = childFileDirectory[page].listFiles(pathname -> !pathname.isHidden() && pathname.getName()
                                                                                                                      .startsWith(this.sqlBackupStrategy.getDatabase()));
                        if (targetFileArray != null) {
                            List<BackupFile> targetFileList = new LinkedList<>();
                            for (File file : targetFileArray) {
                                BackupFile backupFile = new BackupFile();
                                backupFile.setName(file.getName())
                                          .setSize(PrettyMemoryUtil.prettyByteSize(file.length()))
                                          .setDirectory(file.isDirectory())
                                          .setCanRead(file.canRead())
                                          .setCanWrite(file.canWrite())
                                          .setHidden(file.isHidden())
                                          .setLastModifyDate(new Date(file.lastModified()));
                                targetFileList.add(backupFile);
                            }
                            return targetFileList;
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    public List<String> selectBackupDate() {
        File backupParentFileDirectory = this.getBackupParentFileDirectory();
        File[] files = backupParentFileDirectory.listFiles();
        if (files != null) {
            return Arrays.stream(files)
                         .filter(file -> !file.isHidden())
                         .map(File::getName)
                         .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    private File[] getChildFileAndFilter(File dateFileDirectory) {
        return dateFileDirectory.listFiles(pathname -> {
            // 按数字序号过滤目录文件名
            for (int i = 0; i < this.pageCount; i++) {
                if (String.valueOf(i).equals(pathname.getName())) {
                    return true;
                }
            }
            return false;
        });
    }

    private File[] getArrayFilterHiddenAndDirectory(File targetFile) {
        return targetFile.listFiles(pathname -> !pathname.isHidden() && pathname.isDirectory());
    }

    private void createBackupFile(File directory) {
        if (!this.sqlBackupStrategy.backup(directory)) {
            throw new SQLBackupException("Failed to create backup file");
        }
    }

    private void createBackupFileByDefault() {
        // 从0序号划分目录并创建文件
        var childFileDirectory = this.getBackupChildFileDirectory(0);
        // 创建备份
        this.createBackupFile(childFileDirectory);
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
