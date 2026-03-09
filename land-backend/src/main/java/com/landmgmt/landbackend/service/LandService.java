package com.landmgmt.landbackend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.landmgmt.landbackend.dao.LandDao;
import com.landmgmt.landbackend.dao.OwnerDao;
import com.landmgmt.landbackend.model.LandDetails;
import com.landmgmt.landbackend.model.Owner;

@Service
public class LandService {

    private final LandDao landDao;
    private final OwnerDao ownerDao;

    public LandService(LandDao landDao, OwnerDao ownerDao) {
        this.landDao = landDao;
        this.ownerDao = ownerDao;
    }

    public void save(LandDetails land) {
        // Resolve owner — use existing or save new
        if (land.getOwnerId() != null && land.getOwnerId() > 0) {
            Owner existing = ownerDao.findById(land.getOwnerId());
            if (existing == null) throw new RuntimeException("Owner not found: " + land.getOwnerId());
        } else if (land.getOwner() != null) {
            Long generatedId = ownerDao.save(land.getOwner());
            land.setOwnerId(generatedId);
        }
        landDao.save(land);
    }

    public void update(LandDetails land) {
        // Update owner details if provided
        if (land.getOwner() != null && land.getOwner().getOwnerId() != null) {
            ownerDao.update(land.getOwner());
        }
        // Recalculate server-side
        if (land.getLengthInSqft() != null && land.getWidthInSqft() != null
                && land.getPurchaseRatePerSqft() != null) {
            BigDecimal total = land.getLengthInSqft()
                    .multiply(land.getWidthInSqft())
                    .multiply(land.getPurchaseRatePerSqft());
            land.setTotalCost(total);
        }
        if (land.getTotalCost() != null && land.getPaidAmount() != null) {
            land.setBalanceAmount(land.getTotalCost().subtract(land.getPaidAmount()));
        }
        landDao.update(land);
    }

    public List<LandDetails> getAllLands() {
        return landDao.findAll();
    }
}