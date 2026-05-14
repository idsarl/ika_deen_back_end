package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Utilisateur;
import ika_deen.back_end.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Utilisateurs", description = "Gestion des utilisateurs pour le Web Admin")
public class AdminUtilisateurController {

    private final UtilisateurRepository repository;

    @GetMapping
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

    @PutMapping("/{id}/status")
    @Operation(summary = "Activer/Désactiver un utilisateur (Admin)")
    public ResponseEntity<Utilisateur> toggleStatus(@PathVariable String id, @RequestParam boolean active) {
        Utilisateur utilisateur = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setEstActif(active);
        return ResponseEntity.ok(repository.save(utilisateur));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur (Admin)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
