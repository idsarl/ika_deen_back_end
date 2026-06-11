package ika_deen.back_end.controller;

import ika_deen.back_end.dto.AdminUtilisateurCreateRequest;
import ika_deen.back_end.dto.AdminUtilisateurUpdateRequest;
import ika_deen.back_end.entite.Utilisateur;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.UtilisateurRepository;
import ika_deen.back_end.service.AdminUtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Admin - Utilisateurs", description = "Gestion des utilisateurs pour le Web Admin")
public class AdminUtilisateurController {

    private final UtilisateurRepository repository;
    private final AdminUtilisateurService adminUtilisateurService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Lister et filtrer les utilisateurs (Admin)")
    public ResponseEntity<List<Utilisateur>> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) ika_deen.back_end.enumeration.Role role) {

        if (email != null && !email.isEmpty()) {
            return ResponseEntity.ok(repository.findByEmailContainingIgnoreCase(email));
        }
        if (role != null) {
            return ResponseEntity.ok(repository.findByRole(role));
        }
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer un utilisateur (Admin)")
    public ResponseEntity<Utilisateur> create(@Valid @RequestBody AdminUtilisateurCreateRequest request) {
        return new ResponseEntity<>(adminUtilisateurService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Modifier un utilisateur (Admin)")
    public ResponseEntity<Utilisateur> update(
            @PathVariable String id,
            @Valid @RequestBody AdminUtilisateurUpdateRequest request) {
        return ResponseEntity.ok(adminUtilisateurService.update(id, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Activer/Désactiver un utilisateur (Admin)")
    public ResponseEntity<Utilisateur> toggleStatus(@PathVariable String id, @RequestParam boolean active) {
        Utilisateur utilisateur = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        utilisateur.setEstActif(active);
        return ResponseEntity.ok(repository.save(utilisateur));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Supprimer un utilisateur (Admin)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
