-- Script to create tables on Mycrosoft SQL Server
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'client'
) CREATE TABLE dbo.client (
    [id] INT NOT NULL,
    [name] VARCHAR(120),
    [category] VARCHAR(5) DEFAULT NULL,
    [is_active] BIT DEFAULT 0,
    PRIMARY KEY (id)
);

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'device'
) CREATE TABLE dbo.device (
    [id] INT,
    [name] VARCHAR(100),
    [nickname] VARCHAR(100),
    [mac] VARCHAR(30),
    [brand] VARCHAR(30),
    [os] VARCHAR(60),
    [processor] VARCHAR(100),
    [user] VARCHAR(30),
    [serial] VARCHAR(100),
    [model] VARCHAR(100),
    [type] VARCHAR(30),
    [client] VARCHAR(120),
    [client_id] INT DEFAULT NULL,
    [is_active] BIT,
    [last_update] DATETIME,
    [last_sync] DATETIME,
    FOREIGN KEY (client_id) REFERENCES dbo.client (id) ON DELETE
    SET NULL,
        PRIMARY KEY (id)
);

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'ticket'
) CREATE TABLE dbo.ticket (
    [id] INT NOT NULL,
    [code] INT,
    [first_category] VARCHAR(100),
    [second_category] VARCHAR(100),
    [technician] VARCHAR(100),
    [desk] VARCHAR(30),
    [device_id] INT DEFAULT NULL,
    [department] VARCHAR(30),
    [type] VARCHAR(30),
    [priority] VARCHAR(15),
    [client_id] INT DEFAULT NULL,
    [contact] VARCHAR(150),
    [total_hours] VARCHAR(8),
    [origin] VARCHAR(50),
    [status] VARCHAR(20),
    [resp_sla_status] VARCHAR(30),
    [solution_sla_status] VARCHAR(30),
    [rating] INT,
    [creation_date] DATETIME,
    [resp_date] DATETIME,
    [solution_date] DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (device_id) REFERENCES dbo.device (id) ON DELETE
    SET NULL,
        FOREIGN KEY (client_id) REFERENCES dbo.client (id) ON DELETE
    SET NULL
);

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'ticket_queue'
) CREATE TABLE dbo.ticket_queue (
    [id] INT NOT NULL IDENTITY(1, 1),
    [ticket_id] INT,
    [client_id] INT,
    [first_date] DATETIME,
    [second_date] DATETIME,
    [is_first_open] BIT,
    [is_second_open] BIT,
    PRIMARY KEY (id),
    FOREIGN KEY (client_id) REFERENCES dbo.client (id) ON DELETE
    SET NULL
);

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'security_status'
) CREATE TABLE dbo.security_status (
    [id] VARCHAR(30) NOT NULL,
    [name] VARCHAR(100),
    [mac] VARCHAR(30),
    [group] VARCHAR(100),
    [last_sync] DATETIME,
    [is_managed] BIT,
    [is_managed_with_best] BIT,
    [device_id] INT DEFAULT NULL,
    FOREIGN KEY (device_id) REFERENCES dbo.device (id) ON DELETE
    SET NULL,
        PRIMARY KEY (id)
);

CREATE
OR ALTER VIEW device_client_stats AS
SELECT c.name,
    COUNT(d.id) AS "qtd",
    SUM(
        CASE
            WHEN d.last_update < DATEADD(DAY, -45, CURRENT_TIMESTAMP) THEN 1
            ELSE 0
        END
    ) AS "qtd_old",
    COUNT(ss.id) AS "qtd_security",
    c.category,
    c.is_active
FROM dbo.device d
    LEFT JOIN dbo.client c ON c.id = d.client_id
    LEFT JOIN dbo.security_status ss ON ss.device_id = d.id
WHERE d.type IN ('Terminal', 'Notebook', 'Servidor')
GROUP BY c.name,
    c.category,
    c.is_active;

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'backup_log'
) CREATE TABLE dbo.backup_log (
    [id] INT NOT NULL IDENTITY(1, 1),
    [id_device] INT,
    [type] VARCHAR(15),
    [start_date] DATETIME,
    [end_date] DATETIME,
    [has_error] BIT,
    [total_dirs] INT,
    [total_files] INT,
    [total_size] INT,
    [copied_dirs] INT,
    [copied_files] INT,
    [copied_size] INT,
    [failed_dirs] INT,
    [failed_files] INT,
    [failed_size] INT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_device) REFERENCES dbo.device (id) ON DELETE
    SET NULL
);