package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Recitateur;
import ika_deen.back_end.service.FileStorageService;
import ika_deen.back_end.service.RecitateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recitateurs")
@RequiredArgsConstructor
@Tag(name = "Récitateurs", description = "Endpoints pour la gestion des récitateurs du Coran")
public class RecitateurController {

    private final RecitateurService recitateurService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Lister tous les récitateurs")
    public ResponseEntity<List<Recitateur>> getAll() {
        return ResponseEntity.ok(recitateurService.getAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ajouter un nouveau récitateur avec photo (ADMIN)")
    public ResponseEntity<Recitateur> create(
            @RequestParam String nom,
            @RequestParam(required = false) String biographie,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        Recitateur recitateur = Recitateur.builder()
                .nom(nom)
                .biographie(biographie)
                .build();

        if (photo != null && !photo.isEmpty()) {
            recitateur.setPhotoUrl(fileStorageService.storeFile(photo, "images"));
        }

        return new ResponseEntity<>(recitateurService.save(recitateur), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un récitateur (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        recitateurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
