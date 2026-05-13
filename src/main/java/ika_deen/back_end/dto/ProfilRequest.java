package ika_deen.back_end.dto;

import ika_deen.back_end.entite.Profil;
import ika_deen.back_end.enumeration.Genre;
import ika_deen.back_end.enumeration.Langue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour la mise à jour du profil utilisateur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilRequest {
    private String nomAffichage;
    private String avatarUrl;
    private LocalDate dateNaissance;
    private Genre genre;
    private Langue languePreferee;
    private String ville;
    private String pays;
    private double latitude;
    private double longitude;
    
    private Profil.ReglagesPriere reglagesPriere;
    private Profil.PreferencesNotification preferencesNotification;
}
