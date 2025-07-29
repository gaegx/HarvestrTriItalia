package com.gaegxh.harvester.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TicketSearchRequest {
    private String cartId;
    private long departureLocationId;
    private long arrivalLocationId;
    private String departureTime;
    private int adults;
    private int children;
    private Criteria criteria;
}