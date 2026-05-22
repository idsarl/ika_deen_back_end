package ika_deen.back_end.service;

import ika_deen.back_end.dto.AdminStatsResponse;
import ika_deen.back_end.repository.EvenementRepository;
import ika_deen.back_end.repository.MosqueeRepository;
import ika_deen.back_end.repository.PubliciteRepository;
import ika_deen.back_end.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final UtilisateurRepository utilisateurRepository;
    private final MosqueeRepository mosqueeRepository;
    private final EvenementRepository evenementRepository;
    private final PubliciteRepository publiciteRepository;
    private final ValidationService validationService;

    public AdminStatsResponse getGlobalStats() {
        return AdminStatsResponse.builder()
                .totalUtilisateurs(utilisateurRepository.count())
                .totalMosquees(mosqueeRepository.count())
                .totalEvenements(evenementRepository.count())
                .totalPublicites(publiciteRepository.count())
                .totalPublicitesActives(publiciteRepository.findByEstActiveTrue().size())
                .validationsEnAttente(validationService.countPending())
                .inscriptionsParMois(Map.of("total", utilisateurRepository.count()))
                .alertesSysteme(0)
                .build();
    }
}
