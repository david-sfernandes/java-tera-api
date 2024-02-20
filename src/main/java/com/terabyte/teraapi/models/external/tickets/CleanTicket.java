package com.terabyte.teraapi.models.external.tickets;

public record CleanTicket(
  Integer id,
  String categoria_primaria,
  String categoria_secundaria,
  Integer total_avaliacao,
  String tecnico,
  String mesa_trabalho,
  String data_solucao,
  String dispositivo_vinculado,
  String data_resposta,
  String setor,
  String prioridade,
  Integer codigo,
  String cliente,
  String assunto,
  String contato,
  String data_criacao,
  String total_horas,
  String origem,
  String status,
  String urgencia,
  String status_sla_resposta,
  String sla_resposta_tempo,
  String status_sla_solucao,
  String sla_solucao_tempo
) {
}
