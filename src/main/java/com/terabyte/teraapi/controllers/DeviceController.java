package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
  private final MilvusService service = new MilvusService();

  @GetMapping()
  public List<Device> getDevices() {
    return repository.getAll();
  }

  @GetMapping("/sync")
  public ResponseEntity<List<Device>> syncDevices() {
    List<Device> res;
    long start = System.currentTimeMillis();
    try {
      res = service.loadDevices();
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return null;
    }
    repository.batchUpsert(res);
    long end = System.currentTimeMillis();
    System.out.println("Time: " + (end - start) + "ms");
    return ResponseEntity.ok(res);
  }
}
