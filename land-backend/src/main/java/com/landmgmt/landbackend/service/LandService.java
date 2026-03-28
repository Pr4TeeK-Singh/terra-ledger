package com.landmgmt.landbackend.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.landmgmt.landbackend.dao.LandDao;
import com.landmgmt.landbackend.dao.OwnerDao;
import com.landmgmt.landbackend.exception.ValidationException;
import com.landmgmt.landbackend.model.LandDetails;
import com.landmgmt.landbackend.model.Owner;

@Service
public class LandService {

    private static final Logger log = LoggerFactory.getLogger(LandService.class);

    private final LandDao landDao;
    private final OwnerDao ownerDao;

    public LandService(LandDao landDao, OwnerDao ownerDao) {
        this.landDao = landDao;
        this.ownerDao = ownerDao;
    }

    public void save(LandDetails land) {
        if (land.getGaataNumber() == null || land.getGaataNumber().isBlank()) {
            throw new ValidationException("Gaata number is required");
        }
        if (land.getLocationAddress() == null || land.getLocationAddress().isBlank()) {
            throw new ValidationException("Location address is required");
        }

        if (land.getOwnerId() != null && land.getOwnerId() > 0) {
            Owner existing = ownerDao.findById(land.getOwnerId());
            if (existing == null) {
                log.warn("Owner not found with id={}", land.getOwnerId());
                throw new ValidationException("Owner not found: " + land.getOwnerId());
            }
            log.debug("Using existing owner id={}", land.getOwnerId());
        } else if (land.getOwner() != null) {
            log.info("Creating new owner: name={}", land.getOwner().getName());
            Long generatedId = ownerDao.save(land.getOwner());
            land.setOwnerId(generatedId);
            log.info("New owner created with id={}", generatedId);
        }
        landDao.save(land);
    }

    public void update(LandDetails land) {
        if (land.getOwner() != null && land.getOwner().getOwnerId() != null) {
            log.debug("Updating owner id={}", land.getOwner().getOwnerId());
            ownerDao.update(land.getOwner());
        }
        if (land.getLengthInSqft() != null && land.getWidthInSqft() != null
                && land.getPurchaseRatePerSqft() != null) {
            BigDecimal total = land.getLengthInSqft()
                    .multiply(land.getWidthInSqft())
                    .multiply(land.getPurchaseRatePerSqft());
            land.setTotalCost(total);
            log.debug("Recalculated totalCost={} for landId={}", total, land.getLandId());
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