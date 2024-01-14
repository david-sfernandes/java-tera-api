package com.terabyte.teraapi.models;

public record DeviceClientStats(
  String name,
  Integer qtd,
  Integer qtdOld,
  Integer qtdSecurity,
  String category,
  Boolean isActive
) {
  
}
