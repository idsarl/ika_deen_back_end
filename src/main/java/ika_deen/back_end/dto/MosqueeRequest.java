package ika_deen.back_end.dto;

import ika_deen.back_end.entite.Mosquee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO pour la création et la mise à jour d'une mosquée.
 * Permet de découpler l'entité de la couche API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MosqueeRequest {

    // ÉTAPE 1 : Informations de base avec support multilingue
    @NotNull(message = "Le nom est obligatoire")
    private Map<String, String> nom;

    private Map<String, String> description;

    // ÉTAPE 2 : Coordonnées GPS (obligatoires pour la recherche géo)
    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;

    // ÉTAPE 3 : Détails structurels (réutilisation des classes internes de l'entité)
    private Mosquee.Adresse adresse;
    private Mosquee.Contact contact;
    private Mosquee.Equipements equipements;
    private Mosquee.HorairesPriere horairesPriere;
    private Mosquee.Imam imam;
}
