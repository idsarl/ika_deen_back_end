package ika_deen.back_end.repository;

import ika_deen.back_end.entite.HorairesVille;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HorairesVilleRepository extends MongoRepository<HorairesVille, String> {
    /**
     * Recherche les horaires par le nom de la ville.
     * La clause "IgnoreCase" permet de trouver "Bamako" même si l'utilisateur envoie "bamako".
     */
    Optional<HorairesVille> findByNomVilleIgnoreCase(String nomVille);

    /**
     * Vérifie si une ville existe déjà dans la base.
     */
    boolean existsByNomVilleIgnoreCase(String nomVille);
}
