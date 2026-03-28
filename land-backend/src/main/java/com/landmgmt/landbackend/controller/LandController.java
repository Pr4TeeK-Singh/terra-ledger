package com.landmgmt.landbackend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.landmgmt.landbackend.exception.UnauthorizedException;
import com.landmgmt.landbackend.model.LandDetails;
import com.landmgmt.landbackend.security.JwtUtil;
import com.landmgmt.landbackend.service.LandService;

@RestController
@RequestMapping("/api/lands")
@CrossOrigin(origins = "http://localhost:4200")
public class LandController {

    private static final Logger log = LoggerFactory.getLogger(LandController.class);

    private final LandService landService;
    private final JwtUtil jwtUtil;

    public LandController(LandService landService, JwtUtil jwtUtil) {
        this.landService = landService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> saveLand(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody LandDetails land) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        log.info("Saving new land: gaata={}, location={}", land.getGaataNumber(), land.getLocationAddress());
        landService.save(land);
        log.info("Land saved successfully");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLand(@PathVariable Long id, @RequestBody LandDetails land) {
        log.info("Updating land id={}", id);
        land.setLandId(id);
        landService.update(land);
        log.info("Land id={} updated successfully", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<LandDetails> getAllLands() {
        log.info("Fetching all lands");
        List<LandDetails> lands = landService.getAllLands();
        log.info("Returned {} land records", lands.size());
        return lands;
    }
}