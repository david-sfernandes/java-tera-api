package com.terabyte.teraapi.utils.tickets;

import java.util.List;
import java.util.stream.Collectors;

import com.terabyte.teraapi.utils.MilvusMeta;

public record MilvusTicketResp(MilvusMeta meta, List<MilvusTicket> lista) {

  public List<MilvusCleanTicket> format() {
    return lista.stream().map(MilvusTicket::format).collect(Collectors.toList());
  }
}
