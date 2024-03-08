package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.terabyte.teraapi.models.BackupLog;

public class BackupLogRowMapper implements RowMapper<BackupLog> {
    @Override
    public BackupLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BackupLog(
                rs.getInt("id"),
                rs.getInt("id_device"),
                rs.getString("type"),
                rs.getString("start_date"),
                rs.getString("end_date"),
                rs.getBoolean("has_error"),
                rs.getInt("total_dirs"),
                rs.getInt("total_files"),
                rs.getInt("total_size"),
                rs.getInt("copied_dirs"),
                rs.getInt("copied_files"),
                rs.getInt("copied_size"),
                rs.getInt("failed_dirs"),
                rs.getInt("failed_files"),
                rs.getInt("failed_size"));
    }
}
