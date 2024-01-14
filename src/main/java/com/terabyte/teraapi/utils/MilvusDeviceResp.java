package com.terabyte.teraapi.utils;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.repositories.ClientRepository;

public record MilvusDeviceResp(List<MilvusDevice> lista, MilvusMeta meta) {

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
          .clientId(client == null ? null : client.getId())
          .isActive(item.is_ativo())
          .lastUpdate(item.data_ultima_atualizacao())
          .lastSync(lastSync)
          .build();
    }).collect(Collectors.toList());
  }
}
