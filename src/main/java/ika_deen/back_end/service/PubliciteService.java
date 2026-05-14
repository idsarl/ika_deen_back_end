package ika_deen.back_end.service;

import ika_deen.back_end.entite.Publicite;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.PubliciteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PubliciteService {

    private final PubliciteRepository repository;

    public List<Publicite> getActivePublicites() {
        return repository.findByEstActiveTrue();
    }

    public List<Publicite> getAllPublicites() {
        return repository.findAll();
    }

    public Publicite savePublicite(Publicite publicite) {
        return repository.save(publicite);
    }

    public void deletePublicite(String id) {
        repository.deleteById(id);
    }

    public Publicite toggleStatus(String id) {
        Publicite pub = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicité non trouvée"));
        pub.setEstActive(!pub.isEstActive());
        return repository.save(pub);
    }
}
