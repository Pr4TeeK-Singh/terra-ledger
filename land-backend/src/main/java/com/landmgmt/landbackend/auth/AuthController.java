package com.landmgmt.landbackend.auth;

import com.landmgmt.landbackend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final JwtUtil jwtUtil;

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        if (VALID_USERNAME.equals(request.getUsername())
                && VALID_PASSWORD.equals(request.getPassword())) {
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(
                    new AuthResponse(token, request.getUsername(), jwtUtil.getExpirationMs())
            );
        }
        return ResponseEntity.status(401)
                .body(Map.of("error", "Invalid username or password"));
    }
}