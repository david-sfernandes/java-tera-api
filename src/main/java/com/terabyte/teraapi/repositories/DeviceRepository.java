package com.terabyte.teraapi.repositories;

import java.sql.Types;
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

  private final String GET_ALL = "SELECT * FROM dbo.device";
  private final String GET_ID_SECURYTY_STATUS = "SELECT id FROM dbo.device WHERE (mac = ? OR name = ?)";
  private final String CREATE = """
        INSERT INTO dbo.device (id, name, nickname, mac, brand, os, processor, user, serial, model, type, client_id, is_active, last_update, last_sync)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
      """;
  private final String DELETE = "DELETE FROM dbo.device WHERE id = ?";
  private final String UPDATE = """
      UPDATE dbo.device
      SET
          [name] = ?, nickname = ?, mac = ?, brand = ?, os = ?,
          processor = ?, [user]= ?, [serial] = ?, model = ?, [type] = ?,
          client_id = ?, is_active = ?, last_update = ?, last_sync = ?
      WHERE id = ?;
      """;
  // private final String UPSERT = """
  // MERGE INTO dbo.device (`id`, `name`, `nickname`, `mac`, `brand`, `os`,
  // `processor`, `user`, `serial`, `model`, `type`, `client_id`, `is_active`,
  // `last_update`, `last_sync`)
  // KEY (`id`)
  // VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
  // """;
  private final String UPSERT = """
      DECLARE @id INT = ?,
          @name VARCHAR(120) = ?,
          @nickname VARCHAR(120) = ?,
          @mac VARCHAR(120) = ?,
          @brand VARCHAR(30) = ?,
          @os VARCHAR(60) = ?,
          @processor VARCHAR(100) = ?,
          @user VARCHAR(30) = ?,
          @serial VARCHAR(100) = ?,
          @model VARCHAR(100) = ?,
          @type VARCHAR(30) = ?,
          @client_id INT = ?,
          @is_active BIT = ?,
          @last_update VARCHAR(50) = ?,
          @last_sync VARCHAR(50) = ?
      IF EXISTS (SELECT 1
      FROM dbo.device
      WHERE id = @id)
      BEGIN
      -- Atualiza o registro se o ID existir
      UPDATE dbo.device
      SET [name] = @name,
          nickname = @nickname,
          mac = @mac
      WHERE id = @id
      END
      ELSE
      BEGIN
      -- Insere um novo registro se o ID não existir
      INSERT INTO dbo.device
        (id, [name], nickname, mac, brand, os, processor, [user], [serial], model, [type], client_id, is_active, last_update, last_sync)
      VALUES
        (@id, @name, @nickname, @mac, @brand, @os, @processor, @user, @serial, @model, @type, @client_id, @is_active, @last_update, @last_sync)
      END;
      """;

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
    System.out.println("- Upserting devices...");
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
          ps.setObject(12, device.getClientId(), Types.INTEGER);
          ps.setBoolean(13, device.getIsActive());
          ps.setObject(14, device.getLastUpdate(), Types.DATE);
          ps.setDate(15, device.getLastSync());
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
