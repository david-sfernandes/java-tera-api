package com.terabyte.teraapi.utils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.repositories.ClientRepository;

public record MilvusDeviceResp(List<MilvusDevice> lista, Object meta) {
  public List<Device> mapToDevices(ClientRepository clientRepository) {
    List<Device> devices = new ArrayList<>();
    Date now = new Date(System.currentTimeMillis());

    lista.forEach((item) -> {
      Client client = clientRepository.getByName(item.nome_fantasia());
      Device device = Device.builder()
          .id(item.id())
          .name(item.hostname())
          .nickname(item.apelido())
          .mac(item.macaddres())
          .brand(item.placa_mae())
          .os(item.sistema_operacional())
          .processor(item.processador())
          .user(item.usuario_logado())
          .serial(item.numero_serial())
          .model(item.modelo_notebook())
          .type(item.tipo_dispositivo_text())
          .clientId(client.getId())
          .isActive(item.is_ativo())
          .lastUpdate(item.data_ultima_atualizacao())
          .lastSync(now.toString())
          .build();
      devices.add(device);
    });
    return devices;
  }
}
