package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.repositories.DeviceRepository;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
  @Autowired
  private final DeviceRepository deviceRepository = new DeviceRepository();

  @GetMapping()
  public List<Device> getDevices() {
    return deviceRepository.getAll();
  }
}
