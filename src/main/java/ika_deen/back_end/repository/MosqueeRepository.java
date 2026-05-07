package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Mosquee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MosqueeRepository extends MongoRepository<Mosquee, String> {
    Optional<Mosquee> findBySlug(String slug);
}
