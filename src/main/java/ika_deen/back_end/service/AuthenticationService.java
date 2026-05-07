package ika_deen.back_end.service;

import ika_deen.back_end.dto.AuthenticationRequest;
import ika_deen.back_end.dto.AuthenticationResponse;
import ika_deen.back_end.dto.RegisterRequest;
import ika_deen.back_end.entite.*;
import ika_deen.back_end.enumeration.Role;
import ika_deen.back_end.exception.EmailAlreadyExistsException;
import ika_deen.back_end.repository.*;
import ika_deen.back_end.security.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Service gérant la logique métier de l'authentification.
 * Responsable de l'inscription, de la connexion et de l'initialisation de l'administrateur.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UtilisateurRepository repository;
    private final PubliciteRepository publiciteRepository;
    private final RadioRepository radioRepository;
    private final RecitateurRepository recitateurRepository;
    private final MosqueeRepository mosqueeRepository;
    private final ProfilRepository profilRepository;
    private final CommentaireRepository commentaireRepository;
    private final EvenementRepository evenementRepository;
    private final DuaRepository duaRepository;
    private final SourateRepository sourateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${ika-deen.admin.email}")
    private String adminEmail;

    @Value("${ika-deen.admin.password}")
    private String adminPassword;

    /**
     * Méthode exécutée au démarrage de l'application.
     * Crée un compte administrateur par défaut si la base de données est vide.
     * Initialise également les collections vides pour forcer leur création dans MongoDB.
     */
    @PostConstruct
    public void init() {
        // Initialisation de l'Admin
        if (!repository.existsByEmail(adminEmail)) {
            var admin = Utilisateur.builder()
                    .email(adminEmail)
                    .motDePasseHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .estActif(true)
                    .estVerifie(true)
                    .build();
            repository.save(admin);
            System.out.println("Admin par défaut créé avec l'email: " + adminEmail);
        }

        // Création de données de test pour forcer l'apparition des collections dans ika_deen_dev
        if (publiciteRepository.count() == 0) {
            Publicite testPub = Publicite.builder()
                    .imageUrl("https://test-url.com/pub.jpg")
                    .lienDestination("https://ikadeen.com")
                    .estActive(true)
                    .dateCreation(LocalDateTime.now())
                    .build();
            publiciteRepository.save(testPub);
            publiciteRepository.delete(testPub); // On supprime après pour laisser la collection vide mais créée
        }

        if (radioRepository.count() == 0) {
            Radio testRadio = Radio.builder()
                    .nom("Test Radio")
                    .urlStream("https://stream.test")
                    .build();
            radioRepository.save(testRadio);
            radioRepository.delete(testRadio);
        }

        if (recitateurRepository.count() == 0) {
            Recitateur testRecitateur = Recitateur.builder()
                    .nom("Test Recitateur")
                    .build();
            recitateurRepository.save(testRecitateur);
            recitateurRepository.delete(testRecitateur);
        }
        
        System.out.println("Collections MongoDB forcées dans ika_deen_dev.");
    }

    /**
     * Enregistre un nouvel utilisateur dans le système.
     * @param request DTO contenant les informations d'inscription.
     * @return AuthenticationResponse contenant le token JWT généré.
     */
    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("L'adresse email " + request.getEmail() + " est déjà utilisée");
        }

        var utilisateur = Utilisateur.builder()
                .email(request.getEmail())
                .motDePasseHash(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(Role.UTILISATEUR)
                .estActif(true)
                .estVerifie(false)
                .build();

        repository.save(utilisateur);

        var userDetails = User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasseHash())
                .roles(utilisateur.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole().name())
                .message("Compte créé avec succès")
                .build();
    }

    /**
     * Authentifie un utilisateur existant.
     * @param request DTO contenant l'email et le mot de passe.
     * @return AuthenticationResponse contenant le token JWT.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getMotDePasse()
                )
        );

        var utilisateur = repository.findByEmail(request.getEmail())
                .orElseThrow(); // Déjà géré par BadCredentialsException si l'auth échoue

        var userDetails = User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasseHash())
                .roles(utilisateur.getRole().name())
                .build();

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole().name())
                .message("Connexion réussie")
                .build();
    }
}
