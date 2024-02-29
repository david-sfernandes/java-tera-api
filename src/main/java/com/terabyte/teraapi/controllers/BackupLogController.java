package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.BackupLog;
import com.terabyte.teraapi.repositories.BackupLogRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/logs")
public class BackupLogController {
    @Autowired
    private BackupLogRepository backupLogRepository;

    @PostMapping
    public String saveLog(@RequestBody BackupLog backupLog) {
        log.info("Log received");
        System.out.println(backupLog);
        backupLogRepository.create(backupLog);
        return "OK";
    }

    @GetMapping
    public List<BackupLog> getLogs() {
        return backupLogRepository.getAll();
    }
}
