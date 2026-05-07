package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Profil;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilRepository extends MongoRepository<Profil, String> {
    Optional<Profil> findByUtilisateurId(String utilisateurId);
}
