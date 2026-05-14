package ika_deen.back_end.service;

import ika_deen.back_end.entite.Sourate;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.SourateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SourateService {

    private final SourateRepository repository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void seedSourates() {
        if (repository.count() == 0) {
            try {
                InputStream inputStream = new ClassPathResource("data/sourates.json").getInputStream();
                List<Sourate> sourates = objectMapper.readValue(inputStream, new TypeReference<List<Sourate>>() {});
                repository.saveAll(sourates);
                log.info("Coran initialisé avec {} sourates.", sourates.size());
            } catch (Exception e) {
                log.error("Erreur lors de l'initialisation des sourates : {}", e.getMessage());
            }
        }
    }

    public List<Sourate> getAllSourates() {
        return repository.findAll();
    }

    public Sourate getSourateByNumero(int numero) {
        return repository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Sourate non trouvée : " + numero));
    }

    public Sourate saveSourate(Sourate sourate) {
        return repository.save(sourate);
    }

    public void deleteSourate(String id) {
        repository.deleteById(id);
    }
}
