package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Dua;
import ika_deen.back_end.service.DuaService;
import ika_deen.back_end.service.FileStorageService;
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
@RequestMapping("/api/v1/duas")
@RequiredArgsConstructor
@Tag(name = "Duas", description = "Endpoints pour les invocations (Duas)")
public class DuaController {

    private final DuaService duaService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Lister toutes les invocations")
    public ResponseEntity<List<Dua>> getAll() {
        return ResponseEntity.ok(duaService.getAllDuas());
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Filtrer les invocations par catégorie")
    public ResponseEntity<List<Dua>> getByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(duaService.getDuasByCategorie(categorie));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une invocation avec image et audio (ADMIN)")
    public ResponseEntity<Dua> create(
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam String traduction,
            @RequestParam String categorie,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "audio", required = false) MultipartFile audio) {

        Dua dua = Dua.builder()
                .titre(titre)
                .contenu(contenu)
                .traduction(traduction)
                .categorie(categorie)
                .build();

        if (image != null && !image.isEmpty()) {
            dua.setImageUrl(fileStorageService.storeFile(image, "images"));
        }

        if (audio != null && !audio.isEmpty()) {
            dua.setAudioUrl(fileStorageService.storeFile(audio, "audio"));
        }

        return new ResponseEntity<>(duaService.saveDua(dua), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une invocation (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        duaService.deleteDua(id);
        return ResponseEntity.noContent().build();
    }
}
