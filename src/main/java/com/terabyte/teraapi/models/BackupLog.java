package com.terabyte.teraapi.models;

public record BackupLog(
    String hostname,
    String mac,
    String timestampStart,
    String timestampEnd,
    String totalDirs,
    String totalFiles,
    String totalMBytes,
    String copiedDirs,
    String copiedFiles,
    String copiedMBytes,
    String failedDirs,
    String failedFiles,
    String failedMBytes) {

}