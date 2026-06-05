package ika_deen.back_end.controller;

import ika_deen.back_end.dto.DashboardResponse;
import ika_deen.back_end.repository.EvenementRepository;
import ika_deen.back_end.repository.MosqueeRepository;
import ika_deen.back_end.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Endpoints de statistiques pour le Dashboard Web")
public class AdminDashboardController {

    private final UtilisateurRepository utilisateurRepository;
    private final MosqueeRepository mosqueeRepository;
    private final EvenementRepository evenementRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Récupérer les statistiques globales pour l'administrateur")
    public ResponseEntity<DashboardResponse> getDashboardStats() {
        
        Map<String, Long> statsInscriptions = new HashMap<>();
        statsInscriptions.put("Mai", utilisateurRepository.count()); // Simulé pour l'exemple

        DashboardResponse response = DashboardResponse.builder()
                .totalUtilisateurs(utilisateurRepository.count())
                .totalMosquees(mosqueeRepository.count())
                .totalEvenements(evenementRepository.count())
                .inscriptionsParMois(statsInscriptions)
                .alertesSysteme(0) // On pourrait calculer les mosquées sans horaires
                .build();

        return ResponseEntity.ok(response);
    }
}
