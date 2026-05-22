package ika_deen.back_end.service;

import ika_deen.back_end.entite.Radio;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.RadioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RadioService {

    private final RadioRepository repository;
    private final FileStorageService fileStorageService;

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

    public Radio updateRadio(String id, String nom, String urlStream, MultipartFile logo) {
        Radio radio = getRadioById(id);
        if (nom != null && !nom.isBlank()) {
            radio.setNom(nom);
        }
        if (urlStream != null && !urlStream.isBlank()) {
            radio.setUrlStream(urlStream);
        }
        if (logo != null && !logo.isEmpty()) {
            radio.setLogoUrl(fileStorageService.storeFile(logo, "images"));
        }
        return repository.save(radio);
    }

    public void deleteRadio(String id) {
        repository.deleteById(id);
    }
}
