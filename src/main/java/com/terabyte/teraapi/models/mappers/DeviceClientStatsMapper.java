package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.terabyte.teraapi.models.DeviceClientStats;

public class DeviceClientStatsMapper implements RowMapper<DeviceClientStats> {
  @Override
  public DeviceClientStats mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    DeviceClientStats stats = new DeviceClientStats(
        rs.getString("name"),
        rs.getInt("qtd"),
        rs.getInt("qtd_old"),
        rs.getInt("qtd_security"),
        rs.getString("category"),
        rs.getBoolean("is_active"));
    return stats;
  }

}
