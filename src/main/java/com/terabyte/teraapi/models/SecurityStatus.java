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
public class SecurityStatus {
  private String id;
  private String name;
  private String mac;
  private String group;
  private Date lastSync;
  private boolean isManaged;
  private boolean isManagedWithBest;
  private Integer deviceId;
}
