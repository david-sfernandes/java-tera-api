package com.terabyte.teraapi.utils;

import java.util.ArrayList;
import java.util.List;

import com.terabyte.teraapi.models.Client;

public record MilvusClientResp(List<MilvusClient> lista) {
  public List<Client> mapToClient() {
    List<Client> clients = new ArrayList<>();
    lista.forEach((item) -> {
      clients.add(Client.builder()
          .id(item.id())
          .name(item.nome_fantasia())
          .isActive(true)
          .build());
    });
    return clients;
  }
}
