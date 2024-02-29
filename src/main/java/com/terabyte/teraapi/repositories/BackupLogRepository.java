package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.terabyte.teraapi.models.BackupLog;
import com.terabyte.teraapi.models.mappers.BackupLogRowMapper;


public class BackupLogRepository {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM dbo.backup_log";
  private final String CREATE = """
      INSERT INTO dbo.backup_log
          (id_device, type, start_date, end_date, has_error, total_dirs, total_files, total_size, copied_dirs, copied_files, copied_size, failed_dirs, failed_files, failed_size)
      VALUES
          ((SELECT TOP 1 id FROM dbo.device WHERE (mac = ? OR name = ?)), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

  public Integer create(BackupLog backupLog) {
    return jdbcTemplate.update(
        CREATE,
        backupLog.hostname(),
        backupLog.mac(),
        backupLog.type(),
        backupLog.timestampStart(),
        backupLog.timestampEnd(),
        Double.parseDouble(backupLog.failedMBytes()) > 0 ? 1 : 0,
        Integer.parseInt(backupLog.totalDirs()),
        Integer.parseInt(backupLog.totalFiles()),
        Double.parseDouble(backupLog.totalMBytes()),
        Integer.parseInt(backupLog.copiedDirs()),
        Integer.parseInt(backupLog.copiedFiles()),
        Double.parseDouble(backupLog.copiedMBytes()),
        Integer.parseInt(backupLog.failedDirs()),
        Integer.parseInt(backupLog.failedFiles()),
        Double.parseDouble(backupLog.failedMBytes()));
  }

  public List<BackupLog> getAll() {
    return jdbcTemplate.query(GET_ALL, new BackupLogRowMapper());
  }
}
