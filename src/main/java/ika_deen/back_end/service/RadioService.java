package ika_deen.back_end.service;

import ika_deen.back_end.entite.Radio;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.RadioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RadioService {

    private final RadioRepository repository;

    public List<Radio> getAllRadios() {
        return repository.findAll();
    }

    public Radio getRadioById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radio non trouvée"));
    }

    public Radio saveRadio(Radio radio) {
        return repository.save(radio);
    }

    public void deleteRadio(String id) {
        repository.deleteById(id);
    }
}
