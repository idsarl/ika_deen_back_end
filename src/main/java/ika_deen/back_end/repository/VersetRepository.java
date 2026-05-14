package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Verset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersetRepository extends MongoRepository<Verset, String> {
    List<Verset> findBySourateNumeroOrderByVersetNumeroAsc(int sourateNumero);
}
