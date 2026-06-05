package ika_deen.back_end.repository;

import ika_deen.back_end.entite.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByTokenVerification(String token);
    Optional<Utilisateur> findByTelephone(String telephone);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    
    java.util.List<Utilisateur> findByEmailContainingIgnoreCase(String email);
    java.util.List<Utilisateur> findByRole(ika_deen.back_end.enumeration.Role role);
}
