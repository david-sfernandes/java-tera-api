package com.terabyte.teraapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityStatus {
  private Integer id;
  private String name;
  private String mac;
  private String policy;
  private String group;
  private String lastUpdate;
  private Boolean isManaged;
  private Boolean isManagedWithBest;
  private Boolean isPolicyApplied;
  private Integer deviceId;
}
