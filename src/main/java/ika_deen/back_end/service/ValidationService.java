package ika_deen.back_end.service;

import ika_deen.back_end.dto.ValidationPendingResponse;
import ika_deen.back_end.entite.ValidationItem;
import ika_deen.back_end.enumeration.StatutValidation;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.ProfilRepository;
import ika_deen.back_end.repository.ValidationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ValidationRepository validationRepository;
    private final ProfilRepository profilRepository;

    public List<ValidationPendingResponse> getPending() {
        return validationRepository.findByStatutOrderByDateSoumissionDesc(StatutValidation.EN_ATTENTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ValidationPendingResponse approve(String id) {
        ValidationItem item = getPendingItem(id);
        item.setStatut(StatutValidation.APPROUVE);
        item.setDateTraitement(LocalDateTime.now());
        item.setMotifRejet(null);
        return toResponse(validationRepository.save(item));
    }

    public ValidationPendingResponse reject(String id, String motif) {
        ValidationItem item = getPendingItem(id);
        item.setStatut(StatutValidation.REJETE);
        item.setDateTraitement(LocalDateTime.now());
        item.setMotifRejet(motif);
        return toResponse(validationRepository.save(item));
    }

    public long countPending() {
        return validationRepository.countByStatut(StatutValidation.EN_ATTENTE);
    }

    private ValidationItem getPendingItem(String id) {
        ValidationItem item = validationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Validation introuvable : " + id));
        if (item.getStatut() != StatutValidation.EN_ATTENTE) {
            throw new IllegalStateException("Cette validation n'est plus en attente");
        }
        return item;
    }

    private ValidationPendingResponse toResponse(ValidationItem item) {
        String nom = item.getAuteurNom();
        if ((nom == null || nom.isBlank()) && item.getAuteurId() != null) {
            nom = profilRepository.findByUtilisateurId(item.getAuteurId())
                    .map(p -> p.getNomAffichage())
                    .orElse(null);
        }
        return ValidationPendingResponse.builder()
                .id(item.getId())
                .type(item.getType().name())
                .contenu(item.getContenu())
                .auteur(ValidationPendingResponse.Auteur.builder()
                        .id(item.getAuteurId())
                        .email(item.getAuteurEmail())
                        .nom(nom)
                        .build())
                .date(item.getDateSoumission())
                .build();
    }

    @PostConstruct
    void initSampleData() {
        if (validationRepository.count() > 0) {
            return;
        }
        validationRepository.save(ValidationItem.builder()
                .type(ika_deen.back_end.enumeration.TypeValidation.COMMENTAIRE)
                .contenu(Map.of(
                        "mosqueeId", "exemple-mosquee-id",
                        "note", 4,
                        "texte", "Très belle mosquée, accueil chaleureux."
                ))
                .auteurId("exemple-user-id")
                .auteurEmail("utilisateur@example.com")
                .auteurNom("Oumar Dolo")
                .statut(StatutValidation.EN_ATTENTE)
                .dateSoumission(LocalDateTime.now())
                .build());
    }
}
