package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Mosquee;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Mosquee.
 * Gère les interactions avec la collection 'mosquees' dans MongoDB.
 */
@Repository
public interface MosqueeRepository extends MongoRepository<Mosquee, String> {

    // ÉTAPE 1 : Recherche par slug (pour des URLs propres côté Front-end)
    Optional<Mosquee> findBySlug(String slug);

    // ÉTAPE 2 : Vérification d'existence par slug (utile lors de la création/mise à jour)
    boolean existsBySlug(String slug);

    // ÉTAPE 3 : Recherche géographique - Trouve les mosquées à proximité d'un point
    // 'location' est le champ GeoJsonPoint indexé dans l'entité Mosquee
    List<Mosquee> findByLocationNear(Point location, Distance distance);
}
