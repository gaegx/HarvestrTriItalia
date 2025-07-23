package com.gaegxh.harvester.model;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class OfferDetail {
    private String offerName;
    private String serviceName;
    private double price;
    private boolean refundable;
    private boolean changeable;
}
