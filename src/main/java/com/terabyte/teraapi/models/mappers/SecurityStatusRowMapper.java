package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.terabyte.teraapi.models.SecurityStatus;

public class SecurityStatusRowMapper implements RowMapper<SecurityStatus> {
  @Override
  public SecurityStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
    SecurityStatus securityStatus = new SecurityStatus();
    securityStatus.setId(rs.getString("id"));
    securityStatus.setName(rs.getString("name"));
    securityStatus.setMac(rs.getString("mac"));
    securityStatus.setGroup(rs.getString("group"));
    securityStatus.setLastSync(rs.getDate("last_sync"));
    securityStatus.setManaged(rs.getBoolean("is_managed"));
    securityStatus.setManagedWithBest(rs.getBoolean("is_managed_with_best"));
    securityStatus.setDeviceId(rs.getInt("device_id"));
    return securityStatus;
  }

}
