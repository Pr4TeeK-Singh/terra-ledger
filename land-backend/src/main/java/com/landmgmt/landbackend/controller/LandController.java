package com.landmgmt.landbackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.landmgmt.landbackend.model.LandDetails;
import com.landmgmt.landbackend.security.JwtUtil;
import com.landmgmt.landbackend.service.LandService;

@RestController
@RequestMapping("/api/lands")
@CrossOrigin(origins = "http://localhost:4200")
public class LandController {

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        landService.save(land);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLand(@PathVariable Long id, @RequestBody LandDetails land) {
        land.setLandId(id);
        landService.update(land);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<LandDetails> getAllLands() {
        return landService.getAllLands();
    }
}