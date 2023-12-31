package com.terabyte.teraapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
  private Integer id;
  private String name;
  private String category;
  private Boolean isActive;
}
