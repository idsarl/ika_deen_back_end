package ika_deen.back_end.dto;

import ika_deen.back_end.entite.Evenement;
import ika_deen.back_end.entite.Publicite;
import ika_deen.back_end.entite.Verset;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class HomeResponse {
    private ika_deen.back_end.dto.HorairesPriereResponse horairesPriere;
    private Evenement prochainEvenement;
    private List<Publicite> bannières;
    private Verset versetDuJour;
    private String citationDuJour; // Pour un petit message inspirant
}
