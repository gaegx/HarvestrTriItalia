package com.gaegxh.harvester.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TicketClassOption {
    private String serviceClassName;
    private List<OfferDetail> offers;
    private double totalPrice;
}
