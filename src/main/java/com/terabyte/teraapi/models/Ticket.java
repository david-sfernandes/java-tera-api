package com.terabyte.teraapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
  private Integer id;
  private Integer code;
  private String firstCategory;
  private String secondCategory;
  private String technician;
  private String desk;
  private String department;
  private String type;
  private String priority;
  private String clientName;
  private String contact;
  private String totalHours;
  private String origin;
  private String status;
  private String respSlaStatus;
  private String solutionSlaStatus;
  private String creationDate;
  private String respDate;
  private String solutionDate;
  private Integer rating;
  private Integer deviceId;
}
