package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.terabyte.teraapi.models.SecurityStatus;

public class SecurityStatusRowMapper implements RowMapper<SecurityStatus> {
  @Override
  public SecurityStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
    SecurityStatus securityStatus = new SecurityStatus();
    securityStatus.setId(rs.getInt("id"));
    securityStatus.setName(rs.getString("name"));
    securityStatus.setMac(rs.getString("mac"));
    securityStatus.setPolicy(rs.getString("policy"));
    securityStatus.setGroup(rs.getString("group"));
    securityStatus.setLastUpdate(rs.getString("last_update"));
    securityStatus.setIsManaged(rs.getBoolean("is_managed"));
    securityStatus.setIsManagedWithBest(rs.getBoolean("is_managed_with_best"));
    securityStatus.setIsPolicyApplied(rs.getBoolean("is_policy_applied"));
    securityStatus.setDeviceId(rs.getInt("device_id"));
    return securityStatus;
  }

}
