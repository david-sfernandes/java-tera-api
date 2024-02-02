package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.terabyte.teraapi.models.Ticket;
import com.terabyte.teraapi.models.mappers.TicketRowMapper;

@Repository
public class TicketRepository implements JdbcRepository<Ticket> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM dbo.ticket";
  private final String CREATE = """
      INSERT INTO
      dbo.ticket
          (id, code, first_category, second_category, technician, desk, device_id, department, [type], priority, client_name, contact, total_hours, origin, [status], resp_sla_status, solution_sla_status, rating, creation_date, resp_date, solution_date)
      VALUES
          (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
  private final String DELETE = "DELETE FROM dbo.ticket WHERE id = ?";
  private final String UPDATE = """
      UPDATE dbo.ticket
      SET code = ?, first_category = ?, second_category = ?, technician = ?,
      desk = ?, device_id = ?, department = ?, [type] = ?, priority = ?,
      client_name = ?, contact = ?, total_hours = ?, origin = ?, [status] = ?,
      resp_sla_status = ?, solution_sla_status = ?, rating = ?, creation_date = ?,
      resp_date = ?, solution_date = ?
      WHERE id = ?;
      """;
  private final String UPSERT = """
      DECLARE
          @id INT = ?, @code INT = ?, @first_category VARCHAR(100) = ?,
          @second_category VARCHAR(100) = ?, @technician VARCHAR(100) = ?,
          @desk VARCHAR(30) = ?, @device_id INT = ?, @department VARCHAR(30) = ?,
          @type VARCHAR(30) = ?, @priority VARCHAR(15) = ?, @client_id INT = ?,
          @contact VARCHAR(150) = ?, @total_hours VARCHAR(8) = ?, @origin VARCHAR(50) = ?,
          @status VARCHAR(20) = ?, @resp_sla_status VARCHAR(30) = ?, @solution_sla_status VARCHAR(30) = ?,
          @rating INT = ?, @creation_date DATETIME = ?, @resp_date DATETIME = ?, @solution_date DATETIME = ?
      IF EXISTS (SELECT 1
      FROM dbo.ticket
      WHERE id = @id)
      BEGIN
          UPDATE dbo.ticket
          SET code = @code, first_category = @first_category, second_category = @second_category,
          technician = @technician, desk = @desk, device_id = @device_id, department = @department,
          [type] = @type, priority = @priority, client_id = @client_id, contact = @contact,
          total_hours = @total_hours, origin = @origin, [status] = @status, resp_sla_status = @resp_sla_status,
          solution_sla_status = @solution_sla_status, rating = @rating, creation_date = @creation_date,
          resp_date = @resp_date, solution_date = @solution_date
      WHERE id = @id
      END
      ELSE
      BEGIN
          INSERT INTO dbo.ticket
              (id, code, first_category, second_category, technician, desk, device_id, department, [type], priority, client_name, contact, total_hours, origin, [status], resp_sla_status, solution_sla_status, rating, creation_date, resp_date, solution_date)
          VALUES
              (@id, @code, @first_category, @second_category, @technician, @desk, @device_id, @department, @type, @priority, @client_id, @contact, @total_hours, @origin, @status, @resp_sla_status, @solution_sla_status, @rating, @creation_date, @resp_date, @solution_date)
      END;
      """;

  public List<Ticket> getAll() {
    return jdbcTemplate.query(GET_ALL, new TicketRowMapper());
  }

  public Integer create(Ticket ticket) {
    return jdbcTemplate.update(
        CREATE,
        ticket.getId(),
        ticket.getCode(),
        ticket.getFirstCategory(),
        ticket.getSecondCategory(),
        ticket.getTechnician(),
        ticket.getDesk(),
        ticket.getDeviceId(),
        ticket.getDepartment(),
        ticket.getType(),
        ticket.getPriority(),
        ticket.getClientName(),
        ticket.getContact(),
        ticket.getTotalHours(),
        ticket.getOrigin(),
        ticket.getStatus(),
        ticket.getRespSlaStatus(),
        ticket.getSolutionSlaStatus(),
        ticket.getRating(),
        ticket.getCreationDate(),
        ticket.getRespDate(),
        ticket.getSolutionDate());
  }

  public void upsert(Ticket ticket) {
    jdbcTemplate.queryForRowSet(
        UPSERT,
        ticket.getId(),
        ticket.getCode(),
        ticket.getFirstCategory(),
        ticket.getSecondCategory(),
        ticket.getTechnician(),
        ticket.getDesk(),
        ticket.getDeviceId(),
        ticket.getDepartment(),
        ticket.getType(),
        ticket.getPriority(),
        ticket.getClientName(),
        ticket.getContact(),
        ticket.getTotalHours(),
        ticket.getOrigin(),
        ticket.getStatus(),
        ticket.getRespSlaStatus(),
        ticket.getSolutionSlaStatus(),
        ticket.getRating(),
        ticket.getCreationDate(),
        ticket.getRespDate(),
        ticket.getSolutionDate());
  }

  public void batchUpdate(List<Ticket> tickets) {
    jdbcTemplate.batchUpdate(
        UPSERT,
        tickets,
        tickets.size(),
        (ps, ticket) -> {
          ps.setInt(1, ticket.getId());
          ps.setInt(2, ticket.getCode());
          ps.setString(3, ticket.getFirstCategory());
          ps.setString(4, ticket.getSecondCategory());
          ps.setString(5, ticket.getTechnician());
          ps.setString(6, ticket.getDesk());
          ps.setInt(7, ticket.getDeviceId());
          ps.setString(8, ticket.getDepartment());
          ps.setString(9, ticket.getType());
          ps.setString(10, ticket.getPriority());
          ps.setString(11, ticket.getClientName());
          ps.setString(12, ticket.getContact());
          ps.setString(13, ticket.getTotalHours());
          ps.setString(14, ticket.getOrigin());
          ps.setString(15, ticket.getStatus());
          ps.setString(16, ticket.getRespSlaStatus());
          ps.setString(17, ticket.getSolutionSlaStatus());
          ps.setInt(18, ticket.getRating());
          ps.setString(19, ticket.getCreationDate());
          ps.setString(20, ticket.getRespDate());
          ps.setString(21, ticket.getSolutionDate());
        });
  }

  public Integer update(Ticket ticket) {
    return jdbcTemplate.update(
        UPDATE,
        ticket.getCode(),
        ticket.getFirstCategory(),
        ticket.getSecondCategory(),
        ticket.getTechnician(),
        ticket.getDesk(),
        ticket.getDeviceId(),
        ticket.getDepartment(),
        ticket.getType(),
        ticket.getPriority(),
        ticket.getClientName(),
        ticket.getContact(),
        ticket.getTotalHours(),
        ticket.getOrigin(),
        ticket.getStatus(),
        ticket.getRespSlaStatus(),
        ticket.getSolutionSlaStatus(),
        ticket.getRating(),
        ticket.getCreationDate(),
        ticket.getRespDate(),
        ticket.getSolutionDate(),
        ticket.getId());
  }

  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }
}
