package ika_deen.back_end.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private long totalUtilisateurs;
    private long totalMosquees;
    private long totalEvenements;
    private Map<String, Long> inscriptionsParMois; // ex: {"Janvier": 150, "Février": 200}
    private long alertesSysteme; // ex: nombre de mosquées sans horaires à jour
}
