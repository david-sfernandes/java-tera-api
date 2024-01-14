package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.terabyte.teraapi.models.Client;

public class ClientRowMapper implements RowMapper<Client> {
  @Override
  public Client mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    Client client = new Client();
    client.setId(rs.getInt("id"));
    client.setName(rs.getString("name"));
    client.setCategory(rs.getString("category"));
    client.setIsActive(rs.getBoolean("is_active"));
    return client;
  }

}
