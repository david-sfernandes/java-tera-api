package com.terabyte.teraapi.models;

public record BackupLog(
    Integer id,
    Integer idDevice,
    String type,
    String startDate,
    String endDate,
    Boolean hasError,
    Integer totalDirs,
    Integer totalFiles,
    Integer totalSize,
    Integer copiedDirs,
    Integer copiedFiles,
    Integer copiedSize,
    Integer failedDirs,
    Integer failedFiles,
    Integer failedSize
) {
    
}
