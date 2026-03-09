package com.landmgmt.landbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.landmgmt.landbackend.model.LandDetails;
import com.landmgmt.landbackend.service.LandService;

@RestController
@RequestMapping("/api/lands")
@CrossOrigin(origins = "http://localhost:4200")
public class LandController {

    private final LandService landService;

    public LandController(LandService landService) {
        this.landService = landService;
    }

    @PostMapping
    public ResponseEntity<?> saveLand(@RequestBody LandDetails land) {
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