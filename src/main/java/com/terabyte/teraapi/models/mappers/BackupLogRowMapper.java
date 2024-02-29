package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.terabyte.teraapi.models.BackupLog;

public class BackupLogRowMapper implements RowMapper<BackupLog> {
    @Override
    public BackupLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BackupLog(
            rs.getString("hostname"),
            rs.getString("mac"),
            rs.getString("type"),
            rs.getString("start_date"),
            rs.getString("end_date"),
            rs.getString("total_dirs"),
            rs.getString("total_files"),
            rs.getString("total_size"),
            rs.getString("copied_dirs"),
            rs.getString("copied_files"),
            rs.getString("copied_size"),
            rs.getString("failed_dirs"),
            rs.getString("failed_files"),
            rs.getString("failed_size"));
    }
}
