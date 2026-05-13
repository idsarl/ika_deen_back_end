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

    /**
     * ÉTAPE 1 : Calculer les horaires pour une position donnée.
     * Exemple : /api/v1/priere/horaires?lat=9.5&lon=-13.6&methode=MWL
     */
    @GetMapping("/horaires")
    @Operation(summary = "Calculer les horaires de prière pour une position GPS")
    public ResponseEntity<HorairesPriereResponse> getHoraires(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "MWL") MethodeCalcul methode) {
        
        return ResponseEntity.ok(priereService.calculerHoraires(lat, lon, methode));
    }
}
