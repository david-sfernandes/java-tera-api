package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.models.mappers.DeviceRowMapper;

@Repository
public class DeviceRepository implements IRepository<Device> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM device";
  private final String GET_ID_SECURYTY_STATUS = "SELECT id FROM device WHERE (mac = ? OR name = ?)";
  private final String CREATE = """
        INSERT INTO device (id, name, nickname, mac, brand, os, processor, user, serial, model, type, client_id, is_active, last_update, last_sync)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
      """;
  private final String DELETE = "DELETE FROM device WHERE id = ?";
  private final String UPDATE = """
        UPDATE device
        SET name = ?, nickname = ?, mac = ?, brand = ?, os = ?, processor = ?, user = ?, serial = ?, model = ?, type = ?, client_id = ?,
        is_active = ?, last_update = ?, last_sync = ?
        WHERE id = ?;
      """;
  private final String UPSERT = """
        MERGE INTO device (`id`, `name`, `nickname`, `mac`, `brand`, `os`, `processor`, `user`, `serial`, `model`, `type`, `client_id`, `is_active`, `last_update`, `last_sync`)
        KEY (`id`)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
      """;
      
  // private final String UPSERT_SQL = """
  // DECLARE
  // @id INT = ?, @name VARCHAR(120) = ?, @nickname VARCHAR(120) = ?, @mac
  // VARCHAR(120) = ?, @brand VARCHAR(120) = ?, @os VARCHAR(120) = ?, @processor
  // VARCHAR(120) = ?, @user VARCHAR(120) = ?,
  // @serial VARCHAR(120) = ?, @model VARCHAR(120) = ?, @type VARCHAR(120) = ?,
  // @client_id INT = ?, @is_active BIT = ?, @last_update VARCHAR(120) = ?,
  // @last_sync VARCHAR(120) = ?
  // IF EXISTS ((SELECT * FROM device WHERE id = @id) = 1)
  // BEGIN
  // UPDATE device
  // SET name = @name, nickname = @nickname, mac = @mac, brand = @brand, os = @os,
  // processor = @processor, user = @user, serial = @serial, model = @model, type
  // = @type,
  // client_id = @client_id, is_active = @is_active, last_update = @last_update,
  // last_sync = @last_sync
  // WHERE id = @id
  // END
  // ELSE
  // BEGIN
  // INSERT INTO device (id, name, nickname, mac, brand, os, processor, user,
  // serial, model, type, client_id, is_active, last_update, last_sync)
  // VALUES (@id, @name, @nickname, @mac, @brand, @os, @processor, @user, @serial,
  // @model, @type, @client_id, @is_active, @last_update, @last_sync)
  // END
  // """;

  public List<Device> getAll() {
    return jdbcTemplate.query(GET_ALL, new DeviceRowMapper());
  }

  public Integer create(Device device) {
    return jdbcTemplate.update(
        CREATE,
        device.getId(),
        device.getName(),
        device.getNickname(),
        device.getMac(),
        device.getBrand(),
        device.getOs(),
        device.getProcessor(),
        device.getUser(),
        device.getSerial(),
        device.getModel(),
        device.getType(),
        device.getClientId(),
        device.getIsActive(),
        device.getLastUpdate(),
        device.getLastSync());
  }

  public void upsert(Device device) {
    jdbcTemplate.update(
        UPSERT,
        device.getId(),
        device.getName(),
        device.getNickname(),
        device.getMac(),
        device.getBrand(),
        device.getOs(),
        device.getProcessor(),
        device.getUser(),
        device.getSerial(),
        device.getModel(),
        device.getType(),
        device.getClientId(),
        device.getIsActive(),
        device.getLastUpdate(),
        device.getLastSync());
  }

  public void batchUpsert(List<Device> devices) {
    jdbcTemplate.batchUpdate(
        UPSERT,
        devices,
        devices.size(),
        (ps, device) -> {
          ps.setInt(1, device.getId());
          ps.setString(2, device.getName());
          ps.setString(3, device.getNickname());
          ps.setString(4, device.getMac());
          ps.setString(5, device.getBrand());
          ps.setString(6, device.getOs());
          ps.setString(7, device.getProcessor());
          ps.setString(8, device.getUser());
          ps.setString(9, device.getSerial());
          ps.setString(10, device.getModel());
          ps.setString(11, device.getType());
          ps.setInt(12, device.getClientId());
          ps.setBoolean(13, device.getIsActive());
          ps.setString(14, device.getLastUpdate());
          ps.setString(15, device.getLastSync());
        });
    System.out.println("< " + devices.size() + " devices upserted.");
  }

  public Integer update(Device device) {
    return jdbcTemplate.update(
        UPDATE,
        device.getName(),
        device.getNickname(),
        device.getMac(),
        device.getBrand(),
        device.getOs(),
        device.getProcessor(),
        device.getUser(),
        device.getSerial(),
        device.getModel(),
        device.getType(),
        device.getClientId(),
        device.getIsActive(),
        device.getLastUpdate(),
        device.getLastSync(),
        device.getId());
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }

  public List<Integer> getIdSecurityStatus(String mac, String name) {
    return jdbcTemplate.queryForList(GET_ID_SECURYTY_STATUS, Integer.class, mac, name);
  }
}
