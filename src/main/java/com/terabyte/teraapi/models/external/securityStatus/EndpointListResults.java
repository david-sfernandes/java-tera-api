package com.terabyte.teraapi.models.external.securityStatus;

import java.util.List;

public record EndpointListResults(Integer total, Integer page, Integer perPage, Integer pagesCount,
    List<BitSecurityStatus> items) {
}
