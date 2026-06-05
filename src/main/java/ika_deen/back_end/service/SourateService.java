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

    // seedSourates() supprimé : la synchronisation est désormais gérée par QuranSyncService

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
