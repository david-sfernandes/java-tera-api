package com.terabyte.teraapi.models.external.devices;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.models.external.Meta;
import com.terabyte.teraapi.repositories.ClientRepository;

public record DeviceResp(List<DeviceData> lista, Meta meta) {

  public List<Device> mapToDevices(ClientRepository clientRepository) {
    Instant instant = Instant.now();
    Date lastSync = new Date(instant.toEpochMilli());

    return lista.stream().map((item) -> {
      Client client = clientRepository.getByName(item.nome_fantasia());
      String mac = item.macaddres() == null ? null : item.macaddres().replace(":", "").toLowerCase();
      return Device.builder()
          .id(item.id())
          .name(item.hostname())
          .nickname(item.apelido() == null ? null : item.apelido())
          .mac(mac)
          .brand(item.placa_mae())
          .os(item.sistema_operacional())
          .processor(item.processador())
          .user(item.usuario_logado())
          .serial(item.numero_serial())
          .model(item.modelo_notebook())
          .type(item.tipo_dispositivo_text())
          .client(item.nome_fantasia())
          .clientId(client == null ? null : client.getId())
          .isActive(item.is_ativo())
          .lastUpdate(item.data_ultima_atualizacao())
          .lastSync(lastSync)
          .build();
    }).collect(Collectors.toList());
  }

  public List<Device> mapToDevicesTemp(ClientRepository clientRepository) {
    return lista.stream().map((item) -> {
      Client client = clientRepository.getByName(item.nome_fantasia());
      if (client == null) {
        System.out.println("Client not found: " + item.nome_fantasia());
      }
      String mac = item.macaddres() == null ? null : item.macaddres().replace(":", "").toLowerCase();
      return Device.builder()
          .id(item.id())
          .name(item.hostname())
          .nickname(item.apelido() == null ? null : item.apelido())
          .mac(mac)
          .brand(item.placa_mae())
          .os(item.sistema_operacional())
          .processor(item.processador())
          .user(item.usuario_logado())
          .serial(item.numero_serial())
          .model(item.modelo_notebook())
          .type(item.tipo_dispositivo_text())
          .client(item.nome_fantasia())
          .clientId(client == null ? null : client.getId())
          .isActive(item.is_ativo())
          .lastUpdate(item.data_ultima_atualizacao())
          .build();
    }).collect(Collectors.toList());
  }
}
