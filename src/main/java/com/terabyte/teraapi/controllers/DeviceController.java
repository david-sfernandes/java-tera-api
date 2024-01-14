package com.terabyte.teraapi.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.repositories.DeviceRepository;
import com.terabyte.teraapi.services.MilvusService;
import com.terabyte.teraapi.utils.MilvusDeviceResp;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
  @Autowired
  private final DeviceRepository repository = new DeviceRepository();
  @Autowired
  private final MilvusService service = new MilvusService();
  @Autowired 
  private final ClientRepository clientRepository = new ClientRepository();

  private Logger log = LoggerFactory.getLogger("DeviceController");

  @GetMapping()
  public List<Device> getDevices() {
    return repository.getAll();
  }

  @GetMapping("/sync")
  public ResponseEntity<List<Device>> syncDevices() {
    MilvusDeviceResp res;
    long start = System.currentTimeMillis();
    List<Device> devices;
    try {
      res = service.loadDevices();
      devices = res.mapToDevices(clientRepository);
    } catch (Exception e) {
      log.error("# Error: " + e);
      return null;
    }
    repository.batchUpsert(devices);
    long end = System.currentTimeMillis();
    log.info("Time: " + (end - start) + "ms");
    return ResponseEntity.ok(devices);
  }
}
