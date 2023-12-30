CREATE TABLE IF NOT EXISTS `client` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(120),
    `client` VARCHAR(5),
    `is_active` BIT DEFAULT 0,
    PRIMARY KEY (`id`)
)

CREATE TABLE IF NOT EXISTS `device` (
    `id` BIGINT,
    `name` VARCHAR(100),
    `nickname` VARCHAR(100),
    `macaddres` VARCHAR(30),
    `` VARCHAR(30),
    `fabricante` VARCHAR(50),
    `sistema_operacional` VARCHAR(60),
    `placa_mae` VARCHAR(60),
    `processador` VARCHAR(100),
    `usuario_logado` VARCHAR(30),
    `numero_serial` VARCHAR(100),
    `modelo_notebook` VARCHAR(100),
    `tipo_dispositivo_text` VARCHAR(30),
    `is_ativo` BIT,
    `data_ultima_atualizacao` ,
    `last_update` ,
    `nome_fantasia` VARCHAR(100),
)

CREATE TABLE IF NOT EXISTS `ticket` (
    `id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
)

CREATE TABLE IF NOT EXISTS `security_status` (
    `id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
)
