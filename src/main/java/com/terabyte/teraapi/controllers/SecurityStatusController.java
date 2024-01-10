package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.repositories.SecurityStatusRepository;
import com.terabyte.teraapi.services.BitdefenderService;
import com.terabyte.teraapi.utils.BitGroupsResp;

@RestController
@RequestMapping("/api/security-status")
public class SecurityStatusController {
  @Autowired
  private final SecurityStatusRepository securityStatusRepository = new SecurityStatusRepository();
  @Autowired
  private final BitdefenderService bitdefenderService = new BitdefenderService();

  @GetMapping()
  public List<SecurityStatus> getSecurityStatuses() {
    return securityStatusRepository.getAll();
  }

  @GetMapping("/sync")
  public BitGroupsResp syncSecurityStatuses() throws JsonMappingException, JsonProcessingException {
    return bitdefenderService.loadGroups();
  }

  @GetMapping("/syncId")
  public String syncSecurityStatuses(@RequestParam String id) {
    return bitdefenderService.loadStatusByGroupId(id);
  }
}
