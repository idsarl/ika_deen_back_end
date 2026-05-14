package ika_deen.back_end.service;

import ika_deen.back_end.entite.Recitateur;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.RecitateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecitateurService {

    private final RecitateurRepository repository;

    public List<Recitateur> getAll() {
        return repository.findAll();
    }

    public Recitateur getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Récitateur non trouvé"));
    }

    public Recitateur save(Recitateur recitateur) {
        return repository.save(recitateur);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
