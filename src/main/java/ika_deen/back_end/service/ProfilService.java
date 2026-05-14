package ika_deen.back_end.service;

import ika_deen.back_end.dto.ProfilRequest;
import ika_deen.back_end.entite.Profil;
import ika_deen.back_end.entite.Utilisateur;
import ika_deen.back_end.exception.ResourceNotFoundException;
import ika_deen.back_end.repository.ProfilRepository;
import ika_deen.back_end.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service gérant les profils utilisateurs et leurs préférences.
 */
@Service
@RequiredArgsConstructor
public class ProfilService {

    private final ProfilRepository profilRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final GeoLocationService geoLocationService;
    private final jakarta.servlet.http.HttpServletRequest httpServletRequest;

    /**
     * ÉTAPE 1 : Récupérer le profil de l'utilisateur actuellement connecté.
     */
    public Profil getCurrentUserProfile() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        return profilRepository.findByUtilisateurId(utilisateur.getId())
                .orElseGet(() -> createDefaultProfil(utilisateur.getId()));
    }

    /**
     * ÉTAPE 2 : Mettre à jour le profil.
     */
    public Profil updateProfile(ProfilRequest request) {
        Profil profil = getCurrentUserProfile();

        // Mise à jour des champs de base
        if (request.getNomAffichage() != null) profil.setNomAffichage(request.getNomAffichage());
        if (request.getAvatarUrl() != null) profil.setAvatarUrl(request.getAvatarUrl());
        if (request.getDateNaissance() != null) profil.setDateNaissance(request.getDateNaissance());
        if (request.getGenre() != null) profil.setGenre(request.getGenre());
        if (request.getLanguePreferee() != null) profil.setLanguePreferee(request.getLanguePreferee());
        if (request.getVille() != null) profil.setVille(request.getVille());
        if (request.getPays() != null) profil.setPays(request.getPays());

        // Mise à jour de la position GPS (Manuelle)
        if (request.getLatitude() != 0 && request.getLongitude() != 0) {
            profil.setLocation(new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        } 
        // Sinon, on tente une détection automatique si le profil n'a pas encore de position
        else if (profil.getLocation() == null) {
            autoDetectLocation(profil);
        }

        // Mise à jour des préférences
        if (request.getReglagesPriere() != null) profil.setReglagesPriere(request.getReglagesPriere());
        if (request.getPreferencesNotification() != null) profil.setPreferencesNotification(request.getPreferencesNotification());

        return profilRepository.save(profil);
    }

    /**
     * ÉTAPE 3 : Mettre à jour le compteur Tasbih.
     */
    public Profil updateTasbih(int count, String dhikrType) {
        Profil profil = getCurrentUserProfile();
        
        if (profil.getStatistiques() == null) {
            profil.setStatistiques(new Profil.Statistiques());
        }
        
        // Mise à jour du total
        profil.getStatistiques().setTotalTasbih(profil.getStatistiques().getTotalTasbih() + count);
        
        // Mise à jour du détail par type de dhikr
        if (dhikrType != null && !dhikrType.isEmpty()) {
            java.util.Map<String, Integer> details = profil.getStatistiques().getTasbihDetails();
            if (details == null) {
                details = new java.util.HashMap<>();
                profil.getStatistiques().setTasbihDetails(details);
            }
            details.put(dhikrType, details.getOrDefault(dhikrType, 0) + count);
        }
        
        profil.getStatistiques().setDateDerniereActivite(java.time.LocalDate.now());
        
        return profilRepository.save(profil);
    }

    /**
     * ÉTAPE 4 : Mettre à jour l'avatar.
     */
    public Profil updateAvatar(String url) {
        Profil profil = getCurrentUserProfile();
        profil.setAvatarUrl(url);
        return profilRepository.save(profil);
    }

    /**
     * ÉTAPE 5 : Mettre à jour la progression dans le Coran.
     */
    public Profil updateProgression(int sourateNum, int versetNum) {
        Profil profil = getCurrentUserProfile();
        
        if (profil.getProgressionCoran() == null) {
            profil.setProgressionCoran(new Profil.ProgressionCoran());
        }
        
        profil.getProgressionCoran().setDerniereSourate(sourateNum);
        profil.getProgressionCoran().setDernierVerset(versetNum);
        
        return profilRepository.save(profil);
    }

    /**
     * Utilitaire : Créer un profil par défaut si l'utilisateur n'en a pas encore.
     */
    private Profil createDefaultProfil(String utilisateurId) {
        Profil newProfil = Profil.builder()
                .utilisateurId(utilisateurId)
                .nomAffichage("Utilisateur")
                .reglagesPriere(Profil.ReglagesPriere.builder()
                        .methodeCalcul(ika_deen.back_end.enumeration.MethodeCalcul.MWL)
                        .asrJuridique("Standard")
                        .build())
                .preferencesNotification(Profil.PreferencesNotification.builder()
                        .rappelsPriere(true)
                        .minutesAvantRappel(15)
                        .build())
                .build();
        
        // Tentative de géo-détection à la création
        autoDetectLocation(newProfil);
        
        return profilRepository.save(newProfil);
    }

    /**
     * Utilitaire : Détecte automatiquement la position via l'adresse IP.
     */
    private void autoDetectLocation(Profil profil) {
        String ip = httpServletRequest.getRemoteAddr();
        // Si on est derrière un proxy/load balancer (ex: Heroku, Cloudflare)
        String forwardedIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (forwardedIp != null && !forwardedIp.isEmpty()) {
            ip = forwardedIp.split(",")[0];
        }

        GeoLocationService.GeoLocationResponse geo = geoLocationService.getLocationFromIp(ip);
        if (geo != null) {
            profil.setLocation(new GeoJsonPoint(geo.getLon(), geo.getLat()));
            profil.setVille(geo.getCity());
            profil.setPays(geo.getCountry());
            if (profil.getFuseauHoraire() == null) {
                profil.setFuseauHoraire(geo.getTimezone());
            }
        }
    }
}
