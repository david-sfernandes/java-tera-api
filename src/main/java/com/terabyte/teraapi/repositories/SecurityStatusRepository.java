package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.models.mappers.SecurityStatusRowMapper;

@Repository
public class SecurityStatusRepository implements IRepository<SecurityStatus>{
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM security_status";
  private final String CREATE = """
        INSERT INTO security_status (id, name, mac, policy, group, last_update, is_managed, is_managed_with_best, is_policy_applied, device_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      """;
  private final String DELETE = "DELETE FROM security_status WHERE id = ?";
  private final String UPDATE = """
      UPDATE security_status 
      SET name = ?, mac = ?, policy = ?, group = ?, last_update = ?, is_managed = ?, is_managed_with_best = ?, is_policy_applied = ?, device_id = ?
      WHERE id = ?
    """;
  private final String UPSERT = """
      DECLARE 
        @id INT = ?, @name VARCHAR(120) = ?, @mac VARCHAR(120) = ?, @policy VARCHAR(120) = ?, @group VARCHAR(120) = ?, @last_update VARCHAR(120) = ?, 
        @is_managed BIT = ?, @is_managed_with_best BIT = ?, @is_policy_applied BIT = ?, @device_id INT = ?
      IF EXISTS ((SELECT * FROM security_status WHERE id = @id) = 1)
        BEGIN
        UPDATE security_status 
          SET name = @name, mac = @mac, policy = @policy, group = @group, last_update = @last_update, is_managed = @is_managed, 
          is_managed_with_best = @is_managed_with_best, is_policy_applied = @is_policy_applied, device_id = @device_id
          WHERE id = @id
        END
      ELSE
        BEGIN
        INSERT INTO security_status (id, name, mac, policy, group, last_update, is_managed, is_managed_with_best, is_policy_applied, device_id) 
        VALUES (@id, @name, @mac, @policy, @group, @last_update, @is_managed, @is_managed_with_best, @is_policy_applied, @device_id)
        END
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
        securityStatus.getPolicy(),
        securityStatus.getGroup(),
        securityStatus.getLastUpdate(),
        securityStatus.getIsManaged(),
        securityStatus.getIsManagedWithBest(),
        securityStatus.getIsPolicyApplied(),
        securityStatus.getDeviceId());
  }

  public void upsert(SecurityStatus securityStatus) {
    jdbcTemplate.queryForRowSet(
        UPSERT,
        securityStatus.getId(),
        securityStatus.getName(),
        securityStatus.getMac(),
        securityStatus.getPolicy(),
        securityStatus.getGroup(),
        securityStatus.getLastUpdate(),
        securityStatus.getIsManaged(),
        securityStatus.getIsManagedWithBest(),
        securityStatus.getIsPolicyApplied(),
        securityStatus.getDeviceId());
  }

  public void batchUpsert(List<SecurityStatus> securityStatuses) {
    jdbcTemplate.batchUpdate(
        UPSERT,
        securityStatuses,
        securityStatuses.size(),
        (ps, securityStatus) -> {
          ps.setInt(1, securityStatus.getId());
          ps.setString(2, securityStatus.getName());
          ps.setString(3, securityStatus.getMac());
          ps.setString(4, securityStatus.getPolicy());
          ps.setString(5, securityStatus.getGroup());
          ps.setString(6, securityStatus.getLastUpdate());
          ps.setBoolean(7, securityStatus.getIsManaged());
          ps.setBoolean(8, securityStatus.getIsManagedWithBest());
          ps.setBoolean(9, securityStatus.getIsPolicyApplied());
          ps.setInt(10, securityStatus.getDeviceId());
        });
  }

  public Integer update(SecurityStatus securityStatus) {
    return jdbcTemplate.update(
        UPDATE,
        securityStatus.getName(),
        securityStatus.getMac(),
        securityStatus.getPolicy(),
        securityStatus.getGroup(),
        securityStatus.getLastUpdate(),
        securityStatus.getIsManaged(),
        securityStatus.getIsManagedWithBest(),
        securityStatus.getIsPolicyApplied(),
        securityStatus.getDeviceId(),
        securityStatus.getId());
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }
}
