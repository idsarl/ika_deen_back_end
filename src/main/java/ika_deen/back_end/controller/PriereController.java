package ika_deen.back_end.controller;

import ika_deen.back_end.dto.HorairesPriereResponse;
import ika_deen.back_end.enumeration.MethodeCalcul;
import ika_deen.back_end.service.PriereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API pour la consultation des horaires de prière.
 */
@RestController
@RequestMapping("/api/v1/priere")
@RequiredArgsConstructor
@Tag(name = "Prières", description = "Endpoints pour le calcul des horaires de prière")
public class PriereController {

    private final PriereService priereService;
    private final ika_deen.back_end.service.ProfilService profilService;

    /**
     * ÉTAPE 1 : Calculer les horaires pour une position donnée.
     * Si lat/lon ne sont pas fournis, utilise la position du profil utilisateur.
     */
    @GetMapping("/horaires")
    @Operation(summary = "Calculer les horaires de prière (Automatique si connecté)")
    public ResponseEntity<HorairesPriereResponse> getHoraires(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "MWL") MethodeCalcul methode) {
        
        if (lat == null || lon == null) {
            try {
                var profil = profilService.getCurrentUserProfile();
                if (profil.getLocation() != null) {
                    lat = profil.getLocation().getY();
                    lon = profil.getLocation().getX();
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(401).build();
            }
        }
        
        return ResponseEntity.ok(priereService.calculerHoraires(lat, lon, methode));
    }

    /**
     * ÉTAPE 2 : Obtenir la direction de la Qibla.
     */
    @GetMapping("/qibla")
    @Operation(summary = "Obtenir la direction de la Qibla (Automatique si connecté)")
    public ResponseEntity<Double> getQibla(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        if (lat == null || lon == null) {
            try {
                var profil = profilService.getCurrentUserProfile();
                if (profil.getLocation() != null) {
                    lat = profil.getLocation().getY();
                    lon = profil.getLocation().getX();
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(401).build();
            }
        }

        return ResponseEntity.ok(priereService.calculerQibla(lat, lon));
    }
}
