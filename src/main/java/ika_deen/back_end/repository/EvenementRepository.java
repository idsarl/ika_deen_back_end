package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Evenement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementRepository extends MongoRepository<Evenement, String> {
    List<Evenement> findByDateEvenementAfterOrderByDateEvenementAsc(LocalDateTime date);
    List<Evenement> findByMosqueeId(String mosqueeId);
}
