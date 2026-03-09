package com.landmgmt.landbackend.dao;

import java.util.List;

import com.landmgmt.landbackend.model.Owner;

public interface OwnerDao {
    Long save(Owner owner);
    void update(Owner owner);
    Owner findById(Long ownerId);
    List<Owner> findAll();
}