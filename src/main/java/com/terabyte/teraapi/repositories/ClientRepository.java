package com.terabyte.teraapi.repositories;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.DeviceClientStats;
import com.terabyte.teraapi.models.mappers.ClientRowMapper;
import com.terabyte.teraapi.models.mappers.DeviceClientStatsMapper;

@Repository
public class ClientRepository implements IRepository<Client> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private Logger log = LoggerFactory.getLogger("ClientRepository");

  private final String GET_ALL = "SELECT * FROM dbo.client";
  private final String CREATE = "INSERT INTO dbo.client (id, [name], category, is_active) VALUES (?, ?, ?, ?)";
  private final String DELETE = "DELETE FROM dbo.client WHERE id = ?";
  private final String UPDATE = "UPDATE dbo.client SET [name] = ?, category = ?, is_active = ? WHERE id = ?";
  private final String GET_BY_NAME = "SELECT * FROM dbo.client WHERE [name] = ?";
  private final String UPSERT = """
      DECLARE @id INT = ?, @name VARCHAR(120) = ?
      IF EXISTS (SELECT 1
      FROM dbo.client
      WHERE id = @id)
      BEGIN
          UPDATE dbo.client SET [name] = @name WHERE id = @id
      END
      ELSE
      BEGIN
          INSERT INTO dbo.client
              (id, [name])
          VALUES
              (@id, @name)
      END
      """;
  private final String GET_STATS = "SELECT * FROM device_client_stats WHERE [name] IS NOT NULL ORDER BY [name];";

  public List<Client> getAll() {
    return jdbcTemplate.query(GET_ALL, new ClientRowMapper());
  }

  public Integer create(Client client) {
    return jdbcTemplate.update(
        CREATE,
        client.getId(),
        client.getName(),
        client.getCategory(),
        client.getIsActive());
  }

  public void upsert(Client client) {
    jdbcTemplate.queryForRowSet(
        UPSERT,
        client.getId(),
        client.getName(),
        client.getIsActive());
  }

  public Integer update(Client client) {
    return jdbcTemplate.update(
        UPDATE,
        client.getName(),
        client.getCategory(),
        client.getIsActive(),
        client.getId());
  }

  public List<DeviceClientStats> getStats() {
    return jdbcTemplate.query(GET_STATS, new DeviceClientStatsMapper());
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }

  public Client getByName(String name) {
    List<Client> clients = jdbcTemplate.query(GET_BY_NAME, new ClientRowMapper(), name);
    if (clients.size() == 0)
      return null;
    return clients.get(0);
  }

  public void batchUpsert(List<Client> clients) {
    jdbcTemplate.batchUpdate(
        UPSERT,
        clients,
        clients.size(),
        (ps, client) -> {
          ps.setInt(1, client.getId());
          ps.setString(2, client.getName());
        });
    log.info("> " + clients.size() + " clients upserted");
  }
}
