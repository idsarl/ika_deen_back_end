package ika_deen.back_end.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdminStatsResponse {
    private long totalUtilisateurs;
    private long totalMosquees;
    private long totalEvenements;
    private long totalPublicites;
    private long totalPublicitesActives;
    private long validationsEnAttente;
    private Map<String, Long> inscriptionsParMois;
    private long alertesSysteme;
}
