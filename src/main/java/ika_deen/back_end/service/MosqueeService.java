package ika_deen.back_end.service;

import ika_deen.back_end.dto.MosqueeRequest;
import ika_deen.back_end.entite.Mosquee;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.MosqueeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Service gérant la logique métier des mosquées.
 */
@Service
@RequiredArgsConstructor
public class MosqueeService {

    private final MosqueeRepository mosqueeRepository;

    /**
     * ÉTAPE 1 : Récupérer toutes les mosquées.
     */
    public List<Mosquee> getAllMosquees() {
        return mosqueeRepository.findAll();
    }

    /**
     * ÉTAPE 2 : Récupérer une mosquée par son ID.
     */
    public Mosquee getMosqueeById(String id) {
        return mosqueeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mosquée non trouvée avec l'id : " + id));
    }

    /**
     * ÉTAPE 3 : Créer une nouvelle mosquée avec génération automatique de slug.
     */
    public Mosquee createMosquee(MosqueeRequest request) {
        // Extraction du nom français pour le slug
        String nomFr = request.getNom().getOrDefault("fr", "mosquee");
        String slug = generateSlug(nomFr);

        // Garantie de l'unicité du slug
        int count = 1;
        String originalSlug = slug;
        while (mosqueeRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + count++;
        }

        // Mapping DTO -> Entité
        Mosquee mosquee = Mosquee.builder()
                .nom(request.getNom())
                .slug(slug)
                .description(request.getDescription())
                .adresse(request.getAdresse())
                .location(new GeoJsonPoint(request.getLongitude(), request.getLatitude()))
                .contact(request.getContact())
                .equipements(request.getEquipements())
                .horairesPriere(request.getHorairesPriere())
                .imam(request.getImam())
                .evaluationMoyenne(0.0)
                .nombreAvis(0)
                .build();

        return mosqueeRepository.save(mosquee);
    }

    /**
     * ÉTAPE 4 : Mettre à jour une mosquée.
     */
    public Mosquee updateMosquee(String id, MosqueeRequest request) {
        Mosquee existing = getMosqueeById(id);

        existing.setNom(request.getNom());
        existing.setDescription(request.getDescription());
        existing.setAdresse(request.getAdresse());
        existing.setLocation(new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        existing.setContact(request.getContact());
        existing.setEquipements(request.getEquipements());
        existing.setHorairesPriere(request.getHorairesPriere());
        existing.setImam(request.getImam());

        return mosqueeRepository.save(existing);
    }

    /**
     * ÉTAPE 5 : Supprimer une mosquée.
     */
    public void deleteMosquee(String id) {
        if (!mosqueeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossible de supprimer : Mosquée introuvable");
        }
        mosqueeRepository.deleteById(id);
    }

    /**
     * ÉTAPE 6 : Recherche géo-localisée (Mosquées dans un rayon X km).
     */
    public List<Mosquee> findNearby(double lat, double lon, double distanceKm) {
        Point point = new Point(lon, lat);
        Distance distance = new Distance(distanceKm, Metrics.KILOMETERS);
        return mosqueeRepository.findByLocationNear(point, distance);
    }

    /**
     * Utilitaire : Génération d'un slug propre (ex: "Grande Mosquée" -> "grande-mosquee").
     */
    private String generateSlug(String input) {
        if (input == null || input.isEmpty()) return "mosquee";
        
        String nowhitespace = Pattern.compile("\\s+").matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Ajoute une image à la liste des images d'une mosquée.
     */
    public Mosquee addImage(String id, String url, boolean principale) {
        Mosquee mosquee = getMosqueeById(id);
        
        if (mosquee.getImages() == null) {
            mosquee.setImages(new java.util.ArrayList<>());
        }
        
        // Si cette image est marquée comme principale, on décoche les autres
        if (principale) {
            mosquee.getImages().forEach(img -> img.setEstPrincipale(false));
        }
        
        mosquee.getImages().add(new Mosquee.ImageMosquee(url, null, principale));
        return mosqueeRepository.save(mosquee);
    }
}
