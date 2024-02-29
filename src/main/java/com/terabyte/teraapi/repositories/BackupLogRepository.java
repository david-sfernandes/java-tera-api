package com.terabyte.teraapi.repositories;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.BackupLog;
import com.terabyte.teraapi.models.mappers.BackupLogRowMapper;

@Repository
public class BackupLogRepository {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM dbo.backup_log";
  private final String CREATE = """
      INSERT INTO dbo.backup_log
          (id_device, type, start_date, end_date, has_error, total_dirs, total_files, total_size, copied_dirs, copied_files, copied_size, failed_dirs, failed_files, failed_size)
      VALUES
          ((SELECT TOP 1 id FROM dbo.device WHERE (mac = ? OR name = ?)), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

  public Integer create(BackupLog backupLog) {
    try {
      System.out.println(backupLog.timestampStart());
      System.out.println(Timestamp.valueOf(backupLog.timestampStart()));
      System.out.println(Timestamp.valueOf(backupLog.timestampEnd()));
    } catch (Exception e) {
      System.out.println("Error on convert to Date");
      System.out.println(e);
    }

    return jdbcTemplate.update(
        CREATE,
        backupLog.mac(),
        backupLog.hostname(),
        backupLog.type(),
        Timestamp.valueOf(backupLog.timestampStart()),
        Timestamp.valueOf(backupLog.timestampEnd()),
        Double.parseDouble(backupLog.failedMBytes().replace(",", ".")) > 0 ? 1 : 0,
        Integer.parseInt(backupLog.totalDirs()),
        Integer.parseInt(backupLog.totalFiles()),
        Double.parseDouble(backupLog.totalMBytes().replace(",", ".")),
        Integer.parseInt(backupLog.copiedDirs()),
        Integer.parseInt(backupLog.copiedFiles()),
        Double.parseDouble(backupLog.copiedMBytes().replace(",", ".")),
        Integer.parseInt(backupLog.failedDirs()),
        Integer.parseInt(backupLog.failedFiles()),
        Double.parseDouble(backupLog.failedMBytes().replace(",", ".")));
  }

  public List<BackupLog> getAll() {
    return jdbcTemplate.query(GET_ALL, new BackupLogRowMapper());
  }
}
