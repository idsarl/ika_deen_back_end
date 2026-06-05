package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Sourate;
import ika_deen.back_end.service.FileStorageService;
import ika_deen.back_end.service.SourateService;
import ika_deen.back_end.service.VersetService;
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
@RequestMapping("/api/v1/sourates")
@RequiredArgsConstructor
@Tag(name = "Sourates", description = "Endpoints pour la gestion du Coran audio")
public class SourateController {

    private final SourateService sourateService;
    private final VersetService versetService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Lister toutes les sourates")
    public ResponseEntity<List<Sourate>> getAll() {
        return ResponseEntity.ok(sourateService.getAllSourates());
    }

    @GetMapping("/{numero}")
    @Operation(summary = "Obtenir une sourate par son numéro")
    public ResponseEntity<Sourate> getByNumero(@PathVariable int numero) {
        return ResponseEntity.ok(sourateService.getSourateByNumero(numero));
    }

    @GetMapping("/{numero}/versets")
    @Operation(summary = "Récupérer tous les versets (Ayats) d'une sourate")
    public ResponseEntity<List<ika_deen.back_end.entite.Verset>> getVersets(@PathVariable int numero) {
        return ResponseEntity.ok(versetService.getVersetsBySourate(numero));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Créer ou mettre à jour une sourate avec son fichier audio (ADMIN)")
    public ResponseEntity<Sourate> createOrUpdate(
            @RequestParam int numero,
            @RequestParam String nomArabe,
            @RequestParam String nomFrancais,
            @RequestParam String recitateurId,
            @RequestPart(value = "audio", required = false) MultipartFile audio) {

        Sourate sourate;
        try {
            sourate = sourateService.getSourateByNumero(numero);
        } catch (Exception e) {
            sourate = new Sourate();
            sourate.setNumero(numero);
        }

        sourate.setNomArabe(nomArabe);
        sourate.setNomFrancais(nomFrancais);
        sourate.setRecitateurId(recitateurId);

        if (audio != null && !audio.isEmpty()) {
            sourate.setUrlAudio(fileStorageService.storeFile(audio, "audio"));
        }

        return new ResponseEntity<>(sourateService.saveSourate(sourate), HttpStatus.CREATED);
    }
}
