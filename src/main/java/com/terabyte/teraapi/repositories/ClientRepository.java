package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.mappers.ClientRowMapper;

@Repository
public class ClientRepository {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM client";
  private final String CREATE = "INSERT INTO client (id, name, category, is_active) VALUES (?, ?, ?, ?)";
  private final String DELETE = "DELETE FROM client WHERE id = ?";
  private final String UPDATE = "UPDATE client SET name = ?, category = ?, is_active = ? WHERE id = ?";
  private final String UPSERT = """
      DECLARE @id INT = ?, @name VARCHAR(120) = ?, @category VARCHAR(5) = ?, @is_active BIT = ?
      IF EXISTS ((SELECT * FROM client WHERE id = @id) = 1)
        BEGIN
        UPDATE client SET name = @name, category = @category, is_active = @is_active WHERE id = @id
        END
      ELSE
        BEGIN
        INSERT INTO client (id, name, category, is_active) VALUES (@id, @name, @category, @is_active)
        END
      """;

  public List<Client> getAll() {
    return jdbcTemplate.query(GET_ALL, new ClientRowMapper());
  }

  public void create(Client client) {
    jdbcTemplate.update(
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
        client.getCategory(),
        client.getIsActive());
  }

  public void update(Client client) {
    jdbcTemplate.update(
        UPDATE,
        client.getName(),
        client.getCategory(),
        client.getIsActive(),
        client.getId());
  }

  public void delete(Integer id) {
    jdbcTemplate.update(DELETE, id);
  }
}
