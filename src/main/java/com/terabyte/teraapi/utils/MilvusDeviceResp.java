package com.terabyte.teraapi.utils;

import java.util.ArrayList;
import java.util.List;

import com.terabyte.teraapi.models.Device;

public record MilvusDeviceResp(List<MilvusDevice> lista, Object meta) {
  public List<Device> mapToDevices() {
    List<Device> devices = new ArrayList<>();

    lista.forEach((item) -> {
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
          .clientId(0)
          .isActive(item.is_ativo())
          .lastUpdate(item.data_ultima_atualizacao())
          .lastSync("")
          .build();
      devices.add(device);
    });
    return devices;
  }
}
