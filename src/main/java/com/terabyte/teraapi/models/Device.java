package com.terabyte.teraapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
  private Integer clientId;
  private Boolean isActive;
  private String lastUpdate;
  private String lastSync;
}
