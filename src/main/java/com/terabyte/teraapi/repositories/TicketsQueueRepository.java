package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.terabyte.teraapi.models.TicketsQueue;
import com.terabyte.teraapi.models.mappers.TicketsQueueRowMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
  private final String GET_FIRST_TICKET_BY_DATE = "SELECT * FROM dbo.ticket_queue WHERE first_date = ? AND is_first_open = 0";
  private final String GET_SECOND_TICKET_BY_DATE = "SELECT * FROM dbo.ticket_queue WHERE second_date = ? AND is_second_open = 0";
  private final String UPDATE_IS_FIRST_OPEN = "UPDATE dbo.ticket_queue SET is_first_open = ? WHERE id = ?";
  private final String UPDATE_IS_SECOND_OPEN = "UPDATE dbo.ticket_queue SET is_second_open = ? WHERE id = ?";

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

  public List<TicketsQueue> getTicketsToOpenFirst(String date) {
    return jdbcTemplate.query(GET_FIRST_TICKET_BY_DATE, new TicketsQueueRowMapper(), date);
  }

  public List<TicketsQueue> getTicketsToOpenSecond(String date) {
    return jdbcTemplate.query(GET_SECOND_TICKET_BY_DATE, new TicketsQueueRowMapper(), date);
  }

  public void batchUpdateIsFirstOpen(List<TicketsQueue> ticketsQueue, Boolean isFirstOpen) {
    jdbcTemplate.batchUpdate(
        UPDATE_IS_FIRST_OPEN,
        ticketsQueue,
        ticketsQueue.size(),
        (ps, tq) -> {
          ps.setBoolean(1, tq.getIsFirstOpen());
          ps.setInt(2, tq.getId());
        });
  }

  public void batchUpdateIsSecondOpen(List<TicketsQueue> ticketsQueue, Boolean isFirstOpen) {
    jdbcTemplate.batchUpdate(
        UPDATE_IS_SECOND_OPEN,
        ticketsQueue,
        ticketsQueue.size(),
        (ps, tq) -> {
          ps.setBoolean(1, tq.getIsFirstOpen());
          ps.setInt(2, tq.getId());
        });
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
    log.info(ticketsQueue.size() + " ticket inserted");
  }
}
