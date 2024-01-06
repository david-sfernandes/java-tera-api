package com.terabyte.teraapi.utils;

public record MilvusDevice(Integer id, String hostname, String apelido, String ip_interno, String macaddres,
    String marca, String fabricante, Boolean is_ativo, String data_criacao, String ip_externo,
    String data_ultima_atualizacao, String dominio, String sistema_operacional, String sistema_operacional_licenca,
    String placa_mae, String placa_mae_serial, String processador, String versao_client, String observacao,
    String usuario_logado, Integer total_processadores, String numero_serial, String placa_mae_modelo,
    String data_compra, String data_garantia, String modelo_notebook, String nome_fantasia,
    String tipo_dispositivo_text, String tipo_dispositivo_icone) {

}
