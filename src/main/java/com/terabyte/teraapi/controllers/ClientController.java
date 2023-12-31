package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.repositories.ClientRepository;

@RestController("/api/clients")
public class ClientController {
  @Autowired
  private final ClientRepository clientRepository = new ClientRepository();

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
}
