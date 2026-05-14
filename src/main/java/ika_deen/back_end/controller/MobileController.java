package ika_deen.back_end.controller;

import ika_deen.back_end.dto.HomeResponse;
import ika_deen.back_end.entite.Verset;
import ika_deen.back_end.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/mobile")
@RequiredArgsConstructor
@Tag(name = "Mobile Home", description = "Endpoints optimisés pour l'application mobile")
public class MobileController {

    private final PriereService priereService;
    private final EvenementService evenementService;
    private final PubliciteService publiciteService;
    private final VersetService versetService;
    private final ProfilService profilService;

    @GetMapping("/home")
    @Operation(summary = "Récupérer toutes les données de la page d'accueil en un seul appel")
    public ResponseEntity<HomeResponse> getHomeData() {
        var profil = profilService.getCurrentUserProfile();
        
        // 1. Horaires de prière
        ika_deen.back_end.dto.HorairesPriereResponse horaires = null;
        if (profil.getLocation() != null) {
            var methode = (profil.getReglagesPriere() != null && profil.getReglagesPriere().getMethodeCalcul() != null) 
                          ? profil.getReglagesPriere().getMethodeCalcul() 
                          : ika_deen.back_end.enumeration.MethodeCalcul.MWL;
            
            horaires = priereService.calculerHoraires(
                profil.getLocation().getY(), 
                profil.getLocation().getX(), 
                methode
            );
        }

        // 2. Prochain événement
        var events = evenementService.getUpcomingEvenements();
        var prochainEvent = events.isEmpty() ? null : events.get(0);

        // 3. Publicités
        var pubs = publiciteService.getActivePublicites();

        // 4. Verset du jour (aléatoire pour l'exemple)
        var versets = versetService.getVersetsBySourate(1); // On pioche dans la Fatiha par défaut
        Verset versetDuJour = versets.isEmpty() ? null : versets.get(new Random().nextInt(versets.size()));

        HomeResponse response = HomeResponse.builder()
                .horairesPriere(horaires)
                .prochainEvenement(prochainEvent)
                .bannières(pubs)
                .versetDuJour(versetDuJour)
                .citationDuJour("Certes, avec la difficulté vient la facilité.")
                .build();

        return ResponseEntity.ok(response);
    }
}
