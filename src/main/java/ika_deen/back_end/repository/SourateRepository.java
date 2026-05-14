package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Sourate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourateRepository extends MongoRepository<Sourate, String> {
    Optional<Sourate> findByNumero(int numero);
    boolean existsByNumero(int numero);
}
