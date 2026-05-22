package ika_deen.back_end.repository;

import ika_deen.back_end.entite.ValidationItem;
import ika_deen.back_end.enumeration.StatutValidation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ValidationRepository extends MongoRepository<ValidationItem, String> {
    List<ValidationItem> findByStatutOrderByDateSoumissionDesc(StatutValidation statut);
    long countByStatut(StatutValidation statut);
}
