package com.zf1976.ant.auth.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 策略备份工具
 *
 * @author ant
 * Create by Ant on 2021/3/16 8:58 AM
 */
public class MySqlStrategyBackup {

    private final Logger log = LoggerFactory.getLogger("[SQL-BACKUP]");
    private static final String INDEX_END = ";";
    private static final String BLANK = "";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final int DEFAULT_BUFFER_SIZE = 16384;
    private final String mysqlDump;
    private final String mysqlRecover;
    private final Pattern pattern = Pattern.compile("(/)([a-zA-Z]*?)(\\?)");
    private final DataSource dataSource;
    private final String database;

    public MySqlStrategyBackup(DataSource dataSource) {
        this.database = this.extractDatabase(dataSource);
        this.dataSource = dataSource;
        this.mysqlRecover = "mysql --defaults-extra-file=/etc/my.cnf " + this.database + " < ";
        this.mysqlDump = "mysqldump --defaults-extra-file=/etc/my.cnf " + this.database;

    }

    /**
     * 提取URl数据库名
     *
     * @date 2021-05-14 21:03:12
     * @param dataSource 数据源
     * @return {@link String}
     */
    private String extractDatabase(DataSource dataSource) {
        Assert.notNull(dataSource, "数据源无效");
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            final Matcher matcher = this.pattern.matcher(url);
            String database;
            while (matcher.find()) {
                database = matcher.group(2);
                if (database != null)  {
                    return database;
                }
            }
            return null;
        } catch (SQLException e) {
            log.error("数据源无效", e.getCause());
            return null;
        }
    }

    /**
     * 获取链接的数据库
     *
     * @date 2021-05-14 21:04:35
     * @return {@link String}
     */
    private String getDatabase() {
        return this.database;
    }

    /**
     * 备份并生成文件
     *
     * @date 2021-05-14 18:12:23
     * @param fileDirectory 备份目录
     * @return {@link boolean}
     */
    public boolean backup(String fileDirectory) {
        return backup(Paths.get(fileDirectory).toFile());
    }

    /**
     * 备份并生成文件
     *
     * @date 2021-05-14 21:18:18
     * @param path 路径
     * @return {@link boolean}
     */
    public boolean backup(Path path) {
        return this.backup(path.toFile());
    }

    /**
     * 备份并生成文件
     *
     * @date 2021-05-14 21:19:46
     * @param firstPath 首路径
     * @param more 子路径
     * @return {@link boolean}
     */
    public boolean backup(String firstPath, String ...more) {
        return this.backup(Paths.get(firstPath, more));
    }

    /**
     * 备份并生成文件
     *
     * @date 2021-05-14 18:13:11
     * @param fileDirectory 备份目录
     * @return {@link boolean}
     */
    public boolean backup(File fileDirectory) {
        if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
            log.warn("创建目录：" + fileDirectory + " 失败");
        }
        if (fileDirectory.isDirectory() || fileDirectory.exists()) {
            try {
                // 生成策略备份文件
                File backupFile = generationStrategyFile(fileDirectory);
                if (!backupFile.exists() || backupFile.isDirectory()) {
                    log.warn("备份文件路径无效：" + fileDirectory.getAbsolutePath());
                    return false;
                }
                // 执行备份文件命令
                if (executeStrategyCommand(backupFile)) {
                    log.info("备份数据库成功");
                }
                return true;
            } catch (IOException | InterruptedException exception) {
                log.error("备份数据库失败 : {}" + exception.getMessage());
                return false;
            }
        } else {
            log.warn("目录：" + fileDirectory + " 不存在");
        }
        return false;
    }

    /**
     * 备份恢复
     *
     * @date 2021-05-14 18:13:41
     * @param absolutePath 备份文件绝对路径
     * @return {@link boolean}
     */
    public boolean recover(String absolutePath) {
        return recover(Paths.get(absolutePath).toFile());
    }

    /**
     * 备份恢复
     *
     * @date 2021-05-14 18:14:04
     * @param sqlFile 备份SQL文件对象
     * @return {@link boolean}
     */
    public boolean recover(File sqlFile){
        if(sqlFile.exists() && sqlFile.canRead() && sqlFile.length() > 0){
            String absolutePath = sqlFile.getAbsolutePath();
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", mysqlRecover + absolutePath});
                if(p.waitFor() == 0){
                    log.info("数据库恢复成功，数据来源：" + absolutePath);
                    return true;
                } else {
                    log.info("数据库恢复失败，数据来源：" + absolutePath);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 读取sql文件流执行SQL
     *
     * @date 2021-05-14 18:29:21
     * @param sqlInputStream sql文件流
     * @return {@link boolean}
     */
    public boolean recover(InputStream sqlInputStream) {
        try {
            List<String> sqlStatement = readFileByLines(sqlInputStream);
            if (sqlStatement.size() > 0) {
                int num = batchSql(sqlStatement);
                if (num > 0) {
                    log.info("execute complete...");
                    return false;
                } else{
                    log.info("no execute sqlStatement...");
                    return true;
                }
            } else {
                log.info("no execute sqlStatement...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行备份文件命令
     *
     * @date 2021-05-14 18:14:46
     * @param backupFile 备份文件对象
     * @return {@link boolean}
     */
    private boolean executeStrategyCommand(File backupFile) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(mysqlDump);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(exec.getInputStream(), DEFAULT_BUFFER_SIZE);
        if (!this.writeBackupFile(bufferedInputStream, backupFile)) {
            log.warn("写出备份文件失败");
            return false;
        }
        return exec.waitFor() == 0;
    }

    /**
     * 写入备份文件
     *
     * @date 2021-03-21 14:17:22
     * @param bufferedReader 缓冲流
     * @param backupFile 备份文件
     * @return boolean
     */
    private boolean writeBackupFile(BufferedInputStream bufferedReader, File backupFile)  {
        if (bufferedReader == null) {
            log.warn("备份文件命令无效：" + mysqlDump);
            return false;
        }
        try (bufferedReader; BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(backupFile), DEFAULT_BUFFER_SIZE)) {
            byte[] data = new byte[16*1024];
            int len;
            while ((len = bufferedReader.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }


    /**
     * @param sql 包含待执行的SQL语句的ArrayList集合
     * @return int 影响的函数
     */
    private int batchSql(List<String> sql) {
        try (Connection connection = this.dataSource.getConnection();
             Statement st = connection.createStatement()){
            // 进行手动提交
            connection.setAutoCommit(false);
            for (String subSql : sql) {
                st.addBatch(subSql);
            }
            try {
                st.executeBatch();
            } catch (SQLException e) {
                // 执行失败回滚
                connection.rollback();
                throw e;
            }
            return 1;
        } catch (Exception e) {
            log.error("批处理SQl任务失败", e.getCause());
            return 0;
        }
    }

    /**
     * 根据策略生成备份文件
     *
     * @param directory 文件目录
     * @return file
     */
    private File generationStrategyFile(File directory) throws IOException {
        File file = Paths.get(directory.getAbsolutePath(), this.generatorFilename()).toFile();
        if (file.isFile()) {
            throw new UnsupportedOperationException("file path is not supported");
        }
        if (!file.createNewFile()) {
            throw new RuntimeException("failed to create file");
        }
        return file;
    }

    /**
     * 以行为单位读取文件，并将文件的每一行格式化到ArrayList中，常用于读面向行的格式化文件
     *
     * @date 2021-05-14 18:30:54
     * @param sqlInputStream sql文件流
     * @return {@link List<String>}
     */
    private List<String> readFileByLines(InputStream sqlInputStream) throws Exception {
        List<String> sqlList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(sqlInputStream, StandardCharsets.UTF_8))) {
            String tempSql;
            int flag = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempSql = reader.readLine()) != null) {
                // 非空白继续执行
                if (!Objects.equals(BLANK, tempSql.trim())) {
                    flag = handlerFlag(sqlList, sb, tempSql, flag);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return sqlList;
    }

    /**
     * sql处理标记
     *
     * @date 2021-05-14 18:45:27
     * @param sqlList sql列表
     * @param sb string builder
     * @param tempSql sql模版
     * @param flag 标记
     * @return {@link int}
     */
    public int handlerFlag(List<String> sqlList, StringBuffer sb, String tempSql, int flag) {
        if (INDEX_END.equals(tempSql.substring(tempSql.length() - 1))) {
            if (flag == 1) {
                sb.append(tempSql);
                sqlList.add(sb.toString());
                sb.delete(0, sb.length());
                flag = 0;
            } else {
                sqlList.add(tempSql);
            }
        } else {
            flag = 1;
            sb.append(tempSql);
        }
        return flag;
    }

    /**
     * 生成文件名
     *
     * @date 2021-05-14 21:46:03
     * @return {@link String}
     */
    private String generatorFilename() {
        synchronized (DATE_FORMAT) {
            return this.database + "-backup_"  + DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".sql";
        }
    }

}
