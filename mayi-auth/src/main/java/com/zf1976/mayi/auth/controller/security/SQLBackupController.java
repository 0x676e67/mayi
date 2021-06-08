package com.zf1976.mayi.auth.controller.security;

import com.zf1976.mayi.auth.backup.service.MySQLBackupService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ant
 * Create by Ant on 2021/6/8 5:57 上午
 */
@RestController
@RequestMapping(
        value = "/oauth/security/backup"
)
public class SQLBackupController {

    private final MySQLBackupService mySQLBackupService;

    public SQLBackupController(MySQLBackupService mySQLBackupService) {
        this.mySQLBackupService = mySQLBackupService;
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/create")
    DataResult<Void> createBackup(){
        return DataResult.success(this.mySQLBackupService.createBackup());
    }
}
