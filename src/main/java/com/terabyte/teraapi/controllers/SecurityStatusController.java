package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.repositories.SecurityStatusRepository;

@RestController("/api/security-status")
public class SecurityStatusController {
  @Autowired
  private final SecurityStatusRepository securityStatusRepository = new SecurityStatusRepository();

  public List<SecurityStatus> getSecurityStatuses() {
    return securityStatusRepository.getAll();
  }
}
