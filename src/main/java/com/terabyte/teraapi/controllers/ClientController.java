package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.services.MilvusService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
  @Autowired
  private final ClientRepository clientRepository = new ClientRepository();

  @Autowired
  private final MilvusService milvusService = new MilvusService();

  @GetMapping()
  public List<Client> getClients() {
    return clientRepository.getAll();
  }

  @PostMapping("/create")
  public void createClient(Client client) {
    clientRepository.create(client);
  }

  @GetMapping("/stats")
  public void getClientStats() {
    clientRepository.getStats();
  }

  @GetMapping("/sync")
  public ResponseEntity<List<Client>> syncClients() {
    List<Client> res;
    long start = System.currentTimeMillis();
    try {
      res = milvusService.loadClients();
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return null;
    }
    clientRepository.batchUpsert(res);
    long end = System.currentTimeMillis();
    System.out.println("Time: " + (end - start) + "ms");
    return ResponseEntity.ok(res);
  }
}
