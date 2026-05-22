package ika_deen.back_end.entite;

import ika_deen.back_end.enumeration.StatutValidation;
import ika_deen.back_end.enumeration.TypeValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "validations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationItem {

    @Id
    private String id;
    private TypeValidation type;
    private String referenceId;
    private Map<String, Object> contenu;
    private String auteurId;
    private String auteurEmail;
    private String auteurNom;

    @Builder.Default
    private StatutValidation statut = StatutValidation.EN_ATTENTE;

    private LocalDateTime dateSoumission;
    private LocalDateTime dateTraitement;
    private String motifRejet;
}
