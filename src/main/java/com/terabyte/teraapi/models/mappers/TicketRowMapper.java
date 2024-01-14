package com.terabyte.teraapi.models.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.terabyte.teraapi.models.Ticket;

public class TicketRowMapper implements RowMapper<Ticket> {
  @Override
  public Ticket mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    Ticket ticket = new Ticket();
    ticket.setId(rs.getInt("id"));
    ticket.setCode(rs.getInt("code"));
    ticket.setFirstCategory(rs.getString("first_category"));
    ticket.setSecondCategory(rs.getString("second_category"));
    ticket.setTechnician(rs.getString("technician"));
    ticket.setDesk(rs.getString("desk"));
    ticket.setDepartment(rs.getString("department"));
    ticket.setType(rs.getString("type"));
    ticket.setPriority(rs.getString("priority"));
    ticket.setClientName(rs.getString("client_name"));
    ticket.setContact(rs.getString("contact"));
    ticket.setTotalHours(rs.getString("total_hours"));
    ticket.setOrigin(rs.getString("origin"));
    ticket.setStatus(rs.getString("status"));
    ticket.setRespSlaStatus(rs.getString("resp_sla_status"));
    ticket.setSolutionSlaStatus(rs.getString("solution_sla_status"));
    ticket.setCreationDate(rs.getString("creation_date"));
    ticket.setRespDate(rs.getString("resp_date"));
    ticket.setSolutionDate(rs.getString("solution_date"));
    ticket.setRating(rs.getInt("rating"));
    ticket.setDeviceId(rs.getInt("device_id"));
    return ticket;
  }
}
