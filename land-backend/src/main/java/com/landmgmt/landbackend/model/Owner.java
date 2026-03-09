package com.landmgmt.landbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Owner {

    private Long ownerId;
    private String name;
    private String contactNo;
    private String address;
    private String aadharNo;
}