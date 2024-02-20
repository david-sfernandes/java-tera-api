package com.terabyte.teraapi.models.external.securityStatus;

import java.util.List;

public record NetworkGroups(String id, String jsonrpc, List<SecurityGroups> result) {
}