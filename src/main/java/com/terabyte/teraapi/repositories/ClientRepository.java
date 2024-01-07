package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.mappers.ClientRowMapper;

@Repository
public class ClientRepository implements IRepository<Client> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM client";
  private final String CREATE = "INSERT INTO client (id, name, category, is_active) VALUES (?, ?, ?, ?)";
  private final String DELETE = "DELETE FROM client WHERE id = ?";
  private final String UPDATE = "UPDATE client SET name = ?, category = ?, is_active = ? WHERE id = ?";
  private final String GET_BY_NAME = "SELECT * FROM client WHERE name = ?";
  // private final String UPSERT_SQL = """
  //     DECLARE @id INT = ?, @name VARCHAR(120) = ?
  //     IF EXISTS ((SELECT * FROM client WHERE id = @id) = 1)
  //       BEGIN
  //       UPDATE client SET name = @name WHERE id = @id
  //       END
  //     ELSE
  //       BEGIN
  //       INSERT INTO client (id, name) VALUES (@id, @name)
  //       END
  //     """;
  private final String UPSERT = """
      MERGE INTO client (`id`, `name`,`category`,`is_active`)
      KEY (`id`)
      VALUES (?, ?, ?, ?);
      """;
  private final String GET_STATS = "SELECT * FROM device_client_stats;";

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

  public SqlRowSet getStats() {
    return jdbcTemplate.queryForRowSet(GET_STATS);
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }

  public Client getByName(String name) {
    List<Client> clients = jdbcTemplate.query(GET_BY_NAME, new ClientRowMapper(), name);
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
          ps.setString(3, client.getCategory());
          ps.setBoolean(4, client.getIsActive());
        });
  }
}
