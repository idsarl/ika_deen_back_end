package ika_deen.back_end.service;

import ika_deen.back_end.entite.HorairesVille;
import ika_deen.back_end.repository.HorairesVilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HorairesVilleService {

    private final HorairesVilleRepository horairesRepository;
    // Injection du service de géo nécessaire
    private final GeoLocationService geoLocationService;

    /**
     * Méthode protégée : seul un SUPER_ADMIN peut modifier les horaires officiels.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public HorairesVille configurerHoraires(HorairesVille nouvelleConfig) {
        // Recherche si la ville existe déjà pour effectuer une mise à jour
        HorairesVille config = horairesRepository.findByNomVilleIgnoreCase(nouvelleConfig.getNomVille())
                .orElse(new HorairesVille());

        config.setNomVille(nouvelleConfig.getNomVille());
        config.setFajr(nouvelleConfig.getFajr());
        config.setDhuhr(nouvelleConfig.getDhuhr());
        config.setAsr(nouvelleConfig.getAsr());
        config.setMaghrib(nouvelleConfig.getMaghrib());
        config.setIsha(nouvelleConfig.getIsha());
        config.setDateValidite(LocalDate.now());
        config.setSourceAutorite("Imamat Général du Mali");

        return horairesRepository.save(config);
    }

    public HorairesVille getHorairesByVille(String ville) {
        return horairesRepository.findByNomVilleIgnoreCase(ville)
                .orElseThrow(() -> new RuntimeException("Horaires non trouvés pour cette ville"));
    }

    /**
     * Mise à jour des horaires officiels existants.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public HorairesVille updateHoraires(String ville, HorairesVille updateData) {
        HorairesVille existant = horairesRepository.findByNomVilleIgnoreCase(ville)
                .orElseThrow(() -> new RuntimeException("Ville non trouvée : " + ville));

        // Mise à jour des champs
        existant.setFajr(updateData.getFajr());
        existant.setDhuhr(updateData.getDhuhr());
        existant.setAsr(updateData.getAsr());
        existant.setMaghrib(updateData.getMaghrib());
        existant.setIsha(updateData.getIsha());
        existant.setDateValidite(LocalDate.now());

        return horairesRepository.save(existant);
    }

    public HorairesVille getHorairesByGeolocalisation(double lat, double lon) {
        // 1. Convertir les coordonnées GPS en nom de ville
        String ville = geoLocationService.getCityFromLatLon(lat, lon);

        // 2. Utiliser la méthode existante pour récupérer les horaires
        return getHorairesByVille(ville);
    }

}
