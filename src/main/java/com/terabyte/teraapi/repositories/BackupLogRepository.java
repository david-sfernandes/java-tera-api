package com.terabyte.teraapi.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.terabyte.teraapi.models.BackupLog;

public class BackupLogRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String GET_ALL = "SELECT * FROM dbo.backup_log";
    private final String CREATE = """
            INSERT INTO dbo.backup_log
                (id_device, type, start_date, end_date, status, total_dirs, total_files, total_size, copied_dirs, copied_files, copied_size, failed_dirs, failed_files, failed_size)
            VALUES
                ((SELECT), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

    public Integer create(BackupLog backupLog) {
        return jdbcTemplate.update(
            CREATE,
            backupLog.hostname(),
            backupLog.mac(),
            backupLog.type(),
            backupLog.timestampStart(),
            backupLog.getEndDate(),
            backupLog.getStatus(),
            backupLog.getTotalDirs(),
            backupLog.getTotalFiles(),
            backupLog.getTotalSize(),
            backupLog.getCopiedDirs(),
            backupLog.getCopiedFiles(),
            backupLog.getCopiedSize(),
            backupLog.getFailedDirs(),
            backupLog.getFailedFiles(),
            backupLog.getFailedSize());

}
