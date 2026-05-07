package ika_deen.back_end.controller;

import ika_deen.back_end.dto.AuthenticationRequest;
import ika_deen.back_end.dto.AuthenticationResponse;
import ika_deen.back_end.dto.RegisterRequest;
import ika_deen.back_end.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller gérant les endpoints d'authentification.
 * Accessible via /api/v1/auth
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final AuthenticationService service;

    /**
     * Endpoint d'inscription pour les nouveaux utilisateurs.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Endpoint de connexion pour les utilisateurs existants et l'admin.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
