package ika_deen.back_end.repository;

import ika_deen.back_end.entite.NomAllah;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NomAllahRepository extends MongoRepository<NomAllah, String> {
    List<NomAllah> findAllByOrderByNumeroAsc();
}
