package com.terabyte.teraapi.repositories;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.models.mappers.SecurityStatusRowMapper;
import com.terabyte.teraapi.utils.BitSecurityStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SecurityStatusRepository implements JdbcRepository<SecurityStatus> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM dbo.security_status";
  private final String CREATE = """
        INSERT INTO dbo.security_status (id, [name], mac, [group], last_sync, is_managed, is_managed_with_best, device_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      """;
  private final String DELETE = "DELETE FROM dbo.security_status WHERE id = ?";
  private final String UPDATE = """
        UPDATE dbo.security_status
        SET [name] = ?, mac = ?, [group] = ?, last_sync = ?, is_managed = ?, is_managed_with_best = ?, device_id = ?
        WHERE id = ?
      """;
  private final String UPSERT = """
      DECLARE
          @id VARCHAR(30) = ?,
          @name VARCHAR(120) = ?,
          @mac VARCHAR(120) = ?,
          @group VARCHAR(120) = ?,
          @last_sync VARCHAR(50) = ?,
          @is_managed BIT = ?,
          @is_managed_with_best BIT = ?
      IF EXISTS (SELECT 1
      FROM dbo.security_status
      WHERE id = @id)
          BEGIN
      UPDATE dbo.security_status
            SET [name] = @name, mac = @mac, [group] = @group, last_sync = @last_sync, is_managed = @is_managed, is_managed_with_best = @is_managed_with_best, device_id = (SELECT TOP 1 id FROM dbo.device WHERE (mac = @mac OR name = @name))
            WHERE id = @id
      END
        ELSE
          BEGIN
      INSERT INTO dbo.security_status
          (id, [name], mac, [group], last_sync, is_managed, is_managed_with_best, device_id)
      VALUES
          (@id, @name, @mac, @group, @last_sync, @is_managed, @is_managed_with_best, (SELECT TOP 1 id FROM dbo.device WHERE (mac = @mac OR name = @name)))
      END;
      """;

  public List<SecurityStatus> getAll() {
    return jdbcTemplate.query(GET_ALL, new SecurityStatusRowMapper());
  }

  public Integer create(SecurityStatus securityStatus) {
    return jdbcTemplate.update(
        CREATE,
        securityStatus.getId(),
        securityStatus.getName(),
        securityStatus.getMac(),
        securityStatus.getGroup(),
        securityStatus.getLastSync(),
        securityStatus.isManaged(),
        securityStatus.isManagedWithBest(),
        securityStatus.getDeviceId());
  }

  public void upsert(SecurityStatus securityStatus) {
    jdbcTemplate.queryForRowSet(
        UPSERT,
        securityStatus.getId(),
        securityStatus.getName(),
        securityStatus.getMac(),
        securityStatus.getGroup(),
        securityStatus.getLastSync(),
        securityStatus.isManaged(),
        securityStatus.isManagedWithBest(),
        securityStatus.getDeviceId());
  }

  public void batchUpsert(List<BitSecurityStatus> securityStatuses, String group) {
    Instant instant = Instant.now();
    Date lastSync = new Date(instant.toEpochMilli());

    jdbcTemplate.batchUpdate(
        UPSERT,
        securityStatuses,
        securityStatuses.size(),
        (ps, securityStatus) -> {
          Boolean isManagedWithBest = securityStatus.managedWithBest() == null ? false : securityStatus.managedWithBest();
          isManagedWithBest = securityStatus.managedRelay() == null ? isManagedWithBest : securityStatus.managedRelay();

          ps.setString(1, securityStatus.id());
          ps.setString(2, securityStatus.name());
          ps.setString(3, securityStatus.macs().get(0));
          ps.setString(4, group);
          ps.setDate(5, lastSync);
          ps.setBoolean(6, securityStatus.isManaged());
          ps.setBoolean(7, isManagedWithBest);
        });
    log.info("< " + securityStatuses.size() + " security status upserted.");
  }

  public Integer update(SecurityStatus securityStatus) {
    return jdbcTemplate.update(
        UPDATE,
        securityStatus.getName(),
        securityStatus.getMac(),
        securityStatus.getGroup(),
        securityStatus.getLastSync(),
        securityStatus.isManaged(),
        securityStatus.isManagedWithBest(),
        securityStatus.getDeviceId(),
        securityStatus.getId());
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }
}
