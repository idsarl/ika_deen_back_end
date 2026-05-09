package ika_deen.back_end.service;

import ika_deen.back_end.dto.AuthenticationRequest;
import ika_deen.back_end.dto.AuthenticationResponse;
import ika_deen.back_end.dto.RegisterRequest;
import ika_deen.back_end.entite.*;
import ika_deen.back_end.enumeration.Role;
import ika_deen.back_end.exception.AccountNotVerifiedException;
import ika_deen.back_end.exception.EmailAlreadyExistsException;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.exception.TokenExpiredException;
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
import java.util.UUID;

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
    private final EmailService emailService;

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
     * @return AuthenticationResponse contenant un message de succès.
     */
    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("L'adresse email " + request.getEmail() + " est déjà utilisée");
        }

        String token = UUID.randomUUID().toString();
        
        var utilisateur = Utilisateur.builder()
                .email(request.getEmail())
                .motDePasseHash(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(Role.UTILISATEUR)
                .estActif(true)
                .estVerifie(false)
                .tokenVerification(token)
                .dateExpirationToken(LocalDateTime.now().plusHours(24))
                .build();

        repository.save(utilisateur);

        // Envoi de l'email de vérification
        emailService.sendVerificationEmail(utilisateur.getEmail(), token);

        return AuthenticationResponse.builder()
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole().name())
                .message("Compte créé avec succès. Veuillez vérifier votre email pour activer votre compte.")
                .build();
    }

    /**
     * Vérifie le compte d'un utilisateur via son token.
     */
    public void verifyEmail(String token) {
        Utilisateur utilisateur = repository.findByTokenVerification(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de vérification invalide"));

        if (utilisateur.getDateExpirationToken().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Le lien de vérification a expiré");
        }

        utilisateur.setEstVerifie(true);
        utilisateur.setTokenVerification(null);
        utilisateur.setDateExpirationToken(null);
        repository.save(utilisateur);
    }

    /**
     * Renvoie un email de vérification.
     */
    public void resendVerificationEmail(String email) {
        Utilisateur utilisateur = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (utilisateur.isEstVerifie()) {
            throw new RuntimeException("Ce compte est déjà vérifié");
        }

        String token = UUID.randomUUID().toString();
        utilisateur.setTokenVerification(token);
        utilisateur.setDateExpirationToken(LocalDateTime.now().plusHours(24));
        repository.save(utilisateur);

        emailService.sendVerificationEmail(utilisateur.getEmail(), token);
    }

    /**
     * Authentifie un utilisateur existant.
     * @param request DTO contenant l'email et le mot de passe.
     * @return AuthenticationResponse contenant le token JWT.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var utilisateur = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!utilisateur.isEstVerifie()) {
            throw new AccountNotVerifiedException("Veuillez vérifier votre compte par email avant de vous connecter");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getMotDePasse()
                )
        );

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
