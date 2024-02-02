package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.terabyte.teraapi.models.TicketsQueue;
import com.terabyte.teraapi.models.mappers.TicketsQueueRowMapper;

@Repository
public class TicketsQueueRepository implements JdbcRepository<TicketsQueue> {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final String GET_ALL = "SELECT * FROM dbo.ticket_queue";
  private final String CREATE = """
      INSERT INTO
      dbo.ticket_queue
          (ticket_id, client_id, first_date, second_date, is_first_open, is_second_open)
      VALUES
          (?, ?, ?, ?, ?, ?);
        """;
  private final String DELETE = "DELETE FROM dbo.ticket_queue WHERE id = ?";
  private final String UPDATE = """
      UPDATE dbo.ticket_queue
      SET ticket_id = ?, client_id = ?, first_date = ?, second_date = ?,
      is_first_open = ?, is_second_open = ?
      WHERE id = ?;
      """;
  private final String GET_BY_DATE = "SELECT * FROM dbo.ticket_queue WHERE first_date = ? OR second_date = ?";

  @Override
  public List<TicketsQueue> getAll() {
    return jdbcTemplate.query(GET_ALL, new TicketsQueueRowMapper());
  }

  @Override
  public Integer create(TicketsQueue tq) {
    return jdbcTemplate.update(
        CREATE,
        tq.getId(),
        tq.getTicketId(),
        tq.getClientId(),
        tq.getFirstDate(),
        tq.getSecondDate(),
        tq.getIsFirstOpen(),
        tq.getIsSecondOpen());
  }

  @Override
  public Integer update(TicketsQueue tq) {
    return jdbcTemplate.update(
        UPDATE,
        tq.getTicketId(),
        tq.getClientId(),
        tq.getFirstDate(),
        tq.getSecondDate(),
        tq.getIsFirstOpen(),
        tq.getIsSecondOpen(),
        tq.getId());
  }

  @Override
  public Integer delete(Integer id) {
    return jdbcTemplate.update(DELETE, id);
  }

  public List<TicketsQueue> getByDate(String date) {
    return jdbcTemplate.query(GET_BY_DATE, new TicketsQueueRowMapper(), date, date);
  }

  public void batchInsert(List<TicketsQueue> ticketsQueue) {
    jdbcTemplate.batchUpdate(CREATE, ticketsQueue, ticketsQueue.size(), (ps, tq) -> {
      ps.setInt(1, tq.getTicketId());
      ps.setInt(2, tq.getClientId());
      ps.setDate(3, tq.getFirstDate());
      ps.setDate(4, tq.getSecondDate());
      ps.setBoolean(5, tq.getIsFirstOpen());
      ps.setBoolean(6, tq.getIsSecondOpen());
    });
  }
}
