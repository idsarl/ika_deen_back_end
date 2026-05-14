package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Publicite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PubliciteRepository extends MongoRepository<Publicite, String> {
    List<Publicite> findByEstActiveTrue();
}
