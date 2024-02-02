package com.terabyte.teraapi.models;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketsQueue {
  private Integer id;
  private Integer ticketId;
  private Integer clientId;
  private Date firstDate;
  private Date secondDate;
  private Boolean isFirstOpen;
  private Boolean isSecondOpen;
}
