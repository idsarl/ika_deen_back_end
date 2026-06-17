package ika_deen.back_end.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoLocationService {

    private final RestTemplate restTemplate;
    private static final String GEO_API_URL = "http://ip-api.com/json/";

    /**
     * Récupère les coordonnées géographiques à partir d'une adresse IP.
     */
    public GeoLocationResponse getLocationFromIp(String ip) {
        try {
            // Pour les tests en local (localhost), ip-api ne peut pas géolocaliser 127.0.0.1
            if (ip == null || ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                log.info("Localhost détecté, géolocalisation ignorée.");
                return null;
            }

            log.info("Tentative de géolocalisation pour l'IP : {}", ip);
            GeoLocationResponse response = restTemplate.getForObject(GEO_API_URL + ip, GeoLocationResponse.class);

            if (response != null && "success".equals(response.getStatus())) {
                log.info("Géolocalisation réussie : {} - {}, {}", ip, response.getCity(), response.getCountry());
                return response;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la géolocalisation par IP : {}", e.getMessage());
        }
        return null;
    }

    @Data
    public static class GeoLocationResponse {
        private String status;
        private String country;
        private String city;
        private double lat;
        private double lon;
        private String timezone;
    }

    /**
     * Récupère le nom de la ville à partir des coordonnées GPS.
     */
    public String getCityFromLatLon(double lat, double lon) {
        String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f", lat, lon);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("address")) {
                Map<String, String> address = (Map<String, String>) response.get("address");
                // On extrait la ville (ou town, village)
                return address.getOrDefault("city", address.getOrDefault("town", address.get("village")));
            }
        } catch (Exception e) {
            log.error("Erreur Reverse Geocoding : {}", e.getMessage());
        }
        return "Bamako"; // Ville par défaut
    }
}
