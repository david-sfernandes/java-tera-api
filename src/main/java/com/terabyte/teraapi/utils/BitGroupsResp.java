package com.terabyte.teraapi.utils;

import java.util.List;

public record BitGroupsResp(String id, String jsonrpc, List<BitEndpoint> result) {
}