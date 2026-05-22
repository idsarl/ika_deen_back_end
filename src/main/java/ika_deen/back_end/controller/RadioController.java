package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Radio;
import ika_deen.back_end.service.FileStorageService;
import ika_deen.back_end.service.RadioService;
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
@RequestMapping("/api/v1/radios")
@RequiredArgsConstructor
@Tag(name = "Radios", description = "Endpoints pour les radios islamiques")
public class RadioController {

    private final RadioService radioService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Lister toutes les radios disponibles")
    public ResponseEntity<List<Radio>> getAll() {
        return ResponseEntity.ok(radioService.getAllRadios());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle radio avec logo (ADMIN)")
    public ResponseEntity<Radio> create(
            @RequestParam String nom,
            @RequestParam String urlStream,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {

        Radio radio = Radio.builder()
                .nom(nom)
                .urlStream(urlStream)
                .build();

        if (logo != null && !logo.isEmpty()) {
            radio.setLogoUrl(fileStorageService.storeFile(logo, "images"));
        }

        return new ResponseEntity<>(radioService.saveRadio(radio), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une radio (ADMIN)")
    public ResponseEntity<Radio> update(
            @PathVariable String id,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String urlStream,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return ResponseEntity.ok(radioService.updateRadio(id, nom, urlStream, logo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une radio (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        radioService.deleteRadio(id);
        return ResponseEntity.noContent().build();
    }
}
