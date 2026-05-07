package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "mosquees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mosquee {

    @Id
    private String id;

    private Map<String, String> nom; // ex: {"fr": "Mosquée Fayçal", "ar": "مسجد"}

    @Indexed(unique = true)
    private String slug;

    private Map<String, String> description;

    private Adresse adresse;

    @GeoSpatialIndexed
    private GeoJsonPoint position;

    private Contact contact;

    private Equipements equipements;

    private HorairesPriere horairesPriere;

    private List<ImageMosquee> images;

    private Imam imam;

    @Builder.Default
    private double evaluationMoyenne = 0.0;

    @Builder.Default
    private int nombreAvis = 0;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {
        private String rue;
        private String ville;
        private String pays;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        private String telephone;
        private String email;
        private String siteWeb;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Equipements {
        private boolean parking;
        private boolean sectionFemmes;
        private boolean accesHandicapes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HorairesPriere {
        private String fajr;
        private String dhuhr;
        private String asr;
        private String maghrib;
        private String isha;
        private String jumua;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageMosquee {
        private String url;
        private String legende;
        private boolean estPrincipale;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Imam {
        private String nom;
        private String bio;
        private String photoUrl;
    }
}
