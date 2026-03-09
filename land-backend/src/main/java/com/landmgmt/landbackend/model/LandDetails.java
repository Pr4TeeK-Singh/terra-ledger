package com.landmgmt.landbackend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LandDetails {

    private Long landId;
    private String gaataNumber;
    private String locationAddress;
    private BigDecimal lengthInSqft;
    private BigDecimal widthInSqft;
    private BigDecimal purchaseRatePerSqft;
    private BigDecimal totalCost;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractEndDate;

    private Long ownerId;
    private Owner owner;
}