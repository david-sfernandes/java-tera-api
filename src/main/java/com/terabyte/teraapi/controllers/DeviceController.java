package com.terabyte.teraapi.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.repositories.DeviceRepository;
import com.terabyte.teraapi.services.MilvusService;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
  @Autowired
  private final DeviceRepository repository = new DeviceRepository();
  @Autowired
  private final MilvusService milvusService = new MilvusService();

  @GetMapping()
  public List<Device> getDevices() {
    return repository.getAll();
  }

  @GetMapping("/test")
  public List<Device> test() throws IOException {
    return milvusService.test();
  }
}
