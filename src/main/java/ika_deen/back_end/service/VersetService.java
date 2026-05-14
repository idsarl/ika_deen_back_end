package ika_deen.back_end.service;

import ika_deen.back_end.entite.Verset;
import ika_deen.back_end.repository.VersetRepository;
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
public class VersetService {

    private final VersetRepository repository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void seedVersets() {
        if (repository.count() == 0) {
            try {
                InputStream inputStream = new ClassPathResource("data/versets_fatiha.json").getInputStream();
                List<Verset> versets = objectMapper.readValue(inputStream, new TypeReference<List<Verset>>() {});
                repository.saveAll(versets);
                log.info("Versets de la Fatiha initialisés.");
            } catch (Exception e) {
                log.error("Erreur lors de l'initialisation des versets : {}", e.getMessage());
            }
        }
    }

    public List<Verset> getVersetsBySourate(int sourateNumero) {
        return repository.findBySourateNumeroOrderByVersetNumeroAsc(sourateNumero);
    }

    public Verset saveVerset(Verset verset) {
        return repository.save(verset);
    }

    public void saveAll(List<Verset> versets) {
        repository.saveAll(versets);
    }
}
