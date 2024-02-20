package com.terabyte.teraapi.repositories;

import java.sql.Date;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.models.external.devices.DeviceResp;
import com.terabyte.teraapi.models.mappers.DeviceRowMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DeviceRepository implements JdbcRepository<Device> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM dbo.device";
  private final String GET_ID_SECURYTY_STATUS = "SELECT id FROM dbo.device WHERE (mac = ? OR name = ?)";
  private final String CREATE = """
        INSERT INTO dbo.device (id, name, nickname, mac, brand, os, processor, user, serial, model, type, client_id, is_active, last_update, last_sync, client)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
      """;
  private final String DELETE = "DELETE FROM dbo.device WHERE id = ?";
  private final String UPDATE = """
      UPDATE dbo.device
      SET
          [name] = ?, nickname = ?, mac = ?, brand = ?, os = ?,
          processor = ?, [user]= ?, [serial] = ?, model = ?, [type] = ?,
          client_id = ?, is_active = ?, last_update = ?, last_sync = ?, client = ?
      WHERE id = ?;
      """;
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
          @client VARCHAR(120) = ?,
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
          [nickname] = @nickname,
          [mac] = @mac,
          [brand] = @brand,
          [os] = @os,
          [processor] = @processor,
          [user] = @user,
          [serial] = @serial,
          [model] = @model,
          [type] = @type,
          [is_active] = @is_active,
          [last_update] = @last_update,
          [last_sync] = @last_sync,
          [client] = @client,
          [client_id] = (SELECT TOP 1 id FROM dbo.client WHERE name = @client)
      WHERE id = @id
      END
      ELSE
      BEGIN
      -- Insere um novo registro se o ID não existir
      INSERT INTO dbo.device
        (id, [name], nickname, mac, brand, os, processor, [user], [serial], model, [type], client_id, is_active, last_update, last_sync, client)
      VALUES
        (@id, @name, @nickname, @mac, @brand, @os, @processor, @user, @serial, @model, @type, (SELECT TOP 1 id FROM dbo.client WHERE name = @client), @is_active, @last_update, @last_sync, @client)
      END;
      """;
  private final String DELETE_OLD_DEVICES = "DELETE FROM dbo.device WHERE [last_sync] < ?;";

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
        device.getLastSync(),
        device.getClient());
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
        device.getLastSync(),
        device.getClient());
  }

  @SuppressWarnings("null")
  public void batchUpsert(DeviceResp devices) {
    Instant instant = Instant.now();
    Date lastSync = new Date(instant.toEpochMilli());
    log.info("- Upserting devices...");
    if (devices.lista().size() == 0 || devices.lista() == null) {
      log.info("< No devices to upsert.");
      return;
    }
    jdbcTemplate.batchUpdate(
        UPSERT,
        devices.lista(),
        devices.lista().size(),
        (ps, device) -> {
          ps.setInt(1, device.id());
          ps.setString(2, device.hostname());
          ps.setString(3, device.apelido());
          ps.setString(4, device.macaddres());
          ps.setString(5, device.marca());
          ps.setString(6, device.sistema_operacional());
          ps.setString(7, device.processador());
          ps.setString(8, device.usuario_logado());
          ps.setString(9, device.numero_serial());
          ps.setString(10, device.modelo_notebook());
          ps.setString(11, device.tipo_dispositivo_text());
          ps.setString(12, device.nome_fantasia());
          ps.setBoolean(13, device.is_ativo());
          ps.setObject(14, device.data_ultima_atualizacao(), Types.DATE);
          ps.setDate(15, lastSync);
        });
    log.info("< " + devices.lista().size() + " devices upserted.");
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
        device.getClient(),
        device.getId());
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }

  public Integer deleteOldDevices() {
    Instant instant = Instant.now();
    Date lastSync = new Date(instant.toEpochMilli());
    Integer deleted = jdbcTemplate.update(DELETE_OLD_DEVICES, lastSync);
    log.info("< " + deleted + " old devices deleted.");
    return deleted;
  }

  public List<Integer> getIdSecurityStatus(String mac, String name) {
    return jdbcTemplate.queryForList(GET_ID_SECURYTY_STATUS, Integer.class, mac, name);
  }
}
