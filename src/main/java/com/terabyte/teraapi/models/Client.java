package com.terabyte.teraapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
  private Integer id;
  private String name;
  private String category;
  private Boolean isActive;
}
