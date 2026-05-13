package ika_deen.back_end.service;

import ika_deen.back_end.entite.NomAllah;
import ika_deen.back_end.repository.NomAllahRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NomAllahService {

    private final NomAllahRepository repository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * ÉTAPE 1 : Récupérer tous les noms.
     */
    public List<NomAllah> getAllNames() {
        return repository.findAllByOrderByNumeroAsc();
    }

    /**
     * ÉTAPE 2 : Initialisation (Seeding) depuis le fichier JSON.
     */
    @PostConstruct
    public void seedNames() {
        if (repository.count() < 99) {
            try {
                // Charger le fichier JSON depuis les ressources
                java.io.InputStream inputStream = getClass().getResourceAsStream("/data/noms-allah.json");
                if (inputStream == null) {
                    System.err.println("Erreur : Impossible de trouver le fichier noms-allah.json");
                    return;
                }

                List<NomAllah> noms = objectMapper.readValue(inputStream, 
                    new com.fasterxml.jackson.core.type.TypeReference<List<NomAllah>>() {});

                // Pour éviter les doublons si certains noms existent déjà, on peut vider ou filtrer.
                // Ici, on vide et on recrée tout pour être sûr d'avoir les 99 noms propres.
                repository.deleteAll();
                repository.saveAll(noms);
                
                System.out.println("Initialisation complète des 99 noms d'Allah réussie (" + noms.size() + " noms).");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'initialisation des noms d'Allah : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
