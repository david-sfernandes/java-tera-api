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
public class Device {
  private Integer id;
  private String name;
  private String nickname;
  private String mac;
  private String brand;
  private String os;
  private String processor;
  private String user;
  private String serial;
  private String model;
  private String type;
  private String client;
  private Integer clientId;
  private Boolean isActive;
  private String lastUpdate;
  private Date lastSync;
}
