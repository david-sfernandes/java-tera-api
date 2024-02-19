package com.terabyte.teraapi.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.BackupLog;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/logs")
public class BackupLogController {
    @PostMapping
    public void saveLog(@RequestBody BackupLog backupLog) {
        log.info(backupLog.toString());
    }
}
