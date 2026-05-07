package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Recitateur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecitateurRepository extends MongoRepository<Recitateur, String> {
}
