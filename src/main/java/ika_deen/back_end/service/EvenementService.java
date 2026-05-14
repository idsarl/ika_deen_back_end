package ika_deen.back_end.service;

import ika_deen.back_end.entite.Evenement;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository repository;

    public List<Evenement> getUpcomingEvenements() {
        return repository.findByDateEvenementAfterOrderByDateEvenementAsc(LocalDateTime.now());
    }

    public List<Evenement> getEvenementsByMosquee(String mosqueeId) {
        return repository.findByMosqueeId(mosqueeId);
    }

    public Evenement getEvenementById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));
    }

    public Evenement saveEvenement(Evenement evenement) {
        return repository.save(evenement);
    }

    public void deleteEvenement(String id) {
        repository.deleteById(id);
    }
}
