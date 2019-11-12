package com.fombico.enrichment.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class QueryRequest {
    private String queryId;
    private String userId;
    private String storeNumber;
}
