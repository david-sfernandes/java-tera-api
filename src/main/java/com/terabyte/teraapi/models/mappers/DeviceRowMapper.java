package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.terabyte.teraapi.models.Device;

public class DeviceRowMapper implements RowMapper<Device> {
  @Override
  public Device mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    Device device = new Device();
    device.setId(rs.getInt("id"));
    device.setName(rs.getString("name"));
    device.setNickname(rs.getString("nickname"));
    device.setMac(rs.getString("mac"));
    device.setBrand(rs.getString("brand"));
    device.setOs(rs.getString("os"));
    device.setProcessor(rs.getString("processor"));
    device.setUser(rs.getString("user"));
    device.setSerial(rs.getString("serial"));
    device.setModel(rs.getString("model"));
    device.setType(rs.getString("type"));
    device.setClientId(rs.getInt("client_id"));
    device.setIsActive(rs.getBoolean("is_active"));
    device.setLastUpdate(rs.getString("last_update"));
    device.setLastSync(rs.getDate("last_sync"));
    return device;
  }
}
