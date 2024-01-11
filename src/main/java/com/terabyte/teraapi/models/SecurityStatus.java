package com.terabyte.teraapi.models;

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
  private String lastSync;
  private Boolean isManaged;
  private Boolean isManagedWithBest;
  private Integer deviceId;
}
