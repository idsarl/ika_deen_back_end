package ika_deen.back_end.entite;

import ika_deen.back_end.enumeration.Genre;
import ika_deen.back_end.enumeration.Langue;
import ika_deen.back_end.enumeration.MethodeCalcul;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "profils_utilisateurs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profil {

    @Id
    private String id;

    @Indexed(unique = true)
    private String utilisateurId;

    private String nomAffichage;

    private String avatarUrl;

    private LocalDate dateNaissance;

    private Genre genre;

    @Builder.Default
    private Langue languePreferee = Langue.FR;

    private String fuseauHoraire;

    @GeoSpatialIndexed(type = org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String ville;

    private String pays;

    private String fcmToken;

    private ReglagesPriere reglagesPriere;

    private PreferencesNotification preferencesNotification;

    private ProgressionCoran progressionCoran;

    private Statistiques statistiques;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReglagesPriere {
        private MethodeCalcul methodeCalcul;
        private String asrJuridique; // Standard ou Hanafi
        private Ajustements ajustements;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Ajustements {
            private int fajr;
            private int dhuhr;
            private int asr;
            private int maghrib;
            private int isha;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreferencesNotification {
        private boolean rappelsPriere;
        private int minutesAvantRappel;
        private boolean versetQuotidien;
        private boolean hadithQuotidien;
        private boolean notificationsEvenements;
        private boolean notificationsPromotionnelles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressionCoran {
        private int derniereSourate;
        private int dernierVerset;
        private String recitateurIdPrefere;
        private List<String> signets; // Liste d'IDs ou descriptions de versets
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistiques {
        private int totalPrieresEnregistrees;
        private long tempsTotalLectureCoran; // en minutes
        private int totalTasbih;
        private java.util.Map<String, Integer> tasbihDetails; // ex: {"SubhanAllah": 33, "Alhamdulillah": 33}
        private int serieJoursActifs;
        private LocalDate dateDerniereActivite;
    }
}
