package com.terabyte.teraapi.utils;

import java.util.List;

public record BitEndpointListResults(Integer total, Integer page, Integer perPage, Integer pagesCount,
    List<BitSecurityStatus> items) {
}
