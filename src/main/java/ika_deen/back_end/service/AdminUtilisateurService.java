package ika_deen.back_end.service;

import ika_deen.back_end.dto.AdminUtilisateurCreateRequest;
import ika_deen.back_end.dto.AdminUtilisateurUpdateRequest;
import ika_deen.back_end.entite.Utilisateur;
import ika_deen.back_end.exception.EmailAlreadyExistsException;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUtilisateurService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur create(AdminUtilisateurCreateRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("L'adresse email " + request.getEmail() + " est déjà utilisée");
        }
        return repository.save(Utilisateur.builder()
                .email(request.getEmail())
                .motDePasseHash(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(request.getRole())
                .estActif(request.isEstActif())
                .estVerifie(request.isEstVerifie())
                .mosqueeId(request.getMosqueeId())
                .build());
    }

    public Utilisateur update(String id, AdminUtilisateurUpdateRequest request) {
        Utilisateur utilisateur = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        if (request.getEmail() != null && !request.getEmail().equals(utilisateur.getEmail())) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("L'adresse email " + request.getEmail() + " est déjà utilisée");
            }
            utilisateur.setEmail(request.getEmail());
        }
        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        }
        if (request.getTelephone() != null) {
            utilisateur.setTelephone(request.getTelephone());
        }
        if (request.getRole() != null) {
            utilisateur.setRole(request.getRole());
        }
        if (request.getEstActif() != null) {
            utilisateur.setEstActif(request.getEstActif());
        }
        if (request.getEstVerifie() != null) {
            utilisateur.setEstVerifie(request.getEstVerifie());
        }
        if (request.getMosqueeId() != null) {
            utilisateur.setMosqueeId(request.getMosqueeId());
        }
        return repository.save(utilisateur);
    }

    public void assignerMosqueeAUtilisateur(String email, String mosqueeId) {
        Utilisateur utilisateur = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        // On met à jour l'identifiant de la mosquée pour cet utilisateur
        utilisateur.setMosqueeId(mosqueeId);
        repository.save(utilisateur);
    }
}
