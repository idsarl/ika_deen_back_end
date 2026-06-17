package ika_deen.back_end.controller;

import ika_deen.back_end.entite.HorairesVille;
import ika_deen.back_end.service.HorairesVilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/horaires-ville")
@RequiredArgsConstructor
public class HorairesVilleController {

    private final HorairesVilleService horairesService;

    /**
     * Endpoint accessible au Super Admin pour configurer/mettre à jour les horaires
     */
    @PostMapping("/admin/configurer")
    public ResponseEntity<HorairesVille> configurer(@RequestBody HorairesVille config) {
        return ResponseEntity.ok(horairesService.configurerHoraires(config));
    }

    /**
     * Endpoint public pour récupérer les horaires d'une ville spécifique
     */
    @GetMapping("/{ville}")
    public ResponseEntity<HorairesVille> getParVille(@PathVariable String ville) {
        return ResponseEntity.ok(horairesService.getHorairesByVille(ville));
    }

    @PutMapping("/admin/modifier/{ville}")
    public ResponseEntity<HorairesVille> modifier(@PathVariable String ville, @RequestBody HorairesVille config) {
        return ResponseEntity.ok(horairesService.updateHoraires(ville, config));
    }

    /**
     * Endpoint public : reçoit les coordonnées GPS et renvoie les horaires de la ville détectée
     */
    @GetMapping("/geolocalisation")
    public ResponseEntity<HorairesVille> getParGeolocalisation(
            @RequestParam double lat,
            @RequestParam double lon) {
        return ResponseEntity.ok(horairesService.getHorairesByGeolocalisation(lat, lon));
    }
}
