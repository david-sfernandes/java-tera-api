package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.terabyte.teraapi.models.TicketsQueue;

public class TicketsQueueRowMapper implements RowMapper<TicketsQueue>{
  @Override
  public TicketsQueue mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    TicketsQueue ticketsQueue = new TicketsQueue();
    ticketsQueue.setId(rs.getInt("id"));
    ticketsQueue.setTicketId(rs.getInt("ticket_id"));
    ticketsQueue.setClientId(rs.getInt("client_id"));
    ticketsQueue.setFirstDate(rs.getDate("first_date"));
    ticketsQueue.setSecondDate(rs.getDate("second_date"));
    ticketsQueue.setIsFirstOpen(rs.getBoolean("is_first_open"));
    ticketsQueue.setIsSecondOpen(rs.getBoolean("is_second_open"));
    return ticketsQueue;
  }
}
