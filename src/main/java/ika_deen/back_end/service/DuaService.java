package ika_deen.back_end.service;

import ika_deen.back_end.entite.Dua;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.DuaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuaService {

    private final DuaRepository repository;

    public List<Dua> getAllDuas() {
        return repository.findAll();
    }

    public List<Dua> getDuasByCategorie(String categorie) {
        return repository.findByCategorie(categorie);
    }

    public Dua getDuaById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invocation non trouvée"));
    }

    public Dua saveDua(Dua dua) {
        return repository.save(dua);
    }

    public void deleteDua(String id) {
        repository.deleteById(id);
    }
}
