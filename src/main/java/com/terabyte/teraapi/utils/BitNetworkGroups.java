package com.terabyte.teraapi.utils;

import java.util.List;

public record BitNetworkGroups(String id, String jsonrpc, List<BitGroups> result) {
}