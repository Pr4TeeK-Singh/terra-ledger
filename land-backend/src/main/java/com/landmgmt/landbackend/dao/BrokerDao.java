package com.landmgmt.landbackend.dao;

import java.util.List;

import com.landmgmt.landbackend.model.Broker;

public interface BrokerDao {
    Long save(Broker broker);
    List<Broker> findAll();
}