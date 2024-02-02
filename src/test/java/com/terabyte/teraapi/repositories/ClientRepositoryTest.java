package com.terabyte.teraapi.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.DeviceClientStats;

@JdbcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { ClientRepository.class })
public class ClientRepositoryTest {

  private ClientRepository clientRepository;

  @BeforeEach
  public void setup() {
    clientRepository = new ClientRepository();
  }

  @Test
  public void testGetAll() {
    List<Client> clients = clientRepository.getAll();
    assertEquals(0, clients.size());
  }

  @Test
  public void testCreate() {
    Client client = new Client(1, "John Doe", "Category", true);
    Integer result = clientRepository.create(client);
    assertEquals(1, result);
  }

  @Test
  public void testUpsert() {
    Client client = new Client(1, "John Doe", "Category", true);
    clientRepository.upsert(client);
    Client retrievedClient = clientRepository.getByName("John Doe");
    assertNotNull(retrievedClient);
    assertEquals(client.getId(), retrievedClient.getId());
    assertEquals(client.getName(), retrievedClient.getName());
  }

  @Test
  public void testUpdate() {
    Client client = new Client(1, "John Doe", "Category", true);
    Integer result = clientRepository.update(client);
    assertEquals(1, result);
  }

  @Test
  public void testGetStats() {
    List<DeviceClientStats> stats = clientRepository.getStats();
    assertEquals(0, stats.size());
  }

  @Test
  public void testDelete() {
    Integer result = clientRepository.delete(1);
    assertEquals(1, result);
  }

  @Test
  public void testGetByName() {
    Client client = clientRepository.getByName("John Doe");
    assertNull(client);
  }

  @Test
  public void testBatchUpsert() {
    List<Client> clients = Arrays.asList(
        new Client(1, "John Doe", "Category", true),
        new Client(2, "Jane Smith", "Category", true)
    );
    clientRepository.batchUpsert(clients);
    List<Client> retrievedClients = clientRepository.getAll();
    assertEquals(clients.size(), retrievedClients.size());
  }
}