package ika_deen.back_end.service;

import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.Qibla;
import com.batoulapps.adhan.data.DateComponents;
import ika_deen.back_end.dto.HorairesPriereResponse;
import ika_deen.back_end.enumeration.MethodeCalcul;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Service pour le calcul des horaires de prière.
 * Utilise la bibliothèque 'adhan' pour une précision astronomique.
 */
@Service
public class PriereService {

    /**
     * ÉTAPE 1 : Calculer les horaires pour une position et une méthode données.
     */
    public HorairesPriereResponse calculerHoraires(double latitude, double longitude, MethodeCalcul methode) {
        
        // Configuration de la position
        Coordinates coordinates = new Coordinates(latitude, longitude);

        // ÉTAPE 2 : Mapping de notre Enum vers les paramètres de la bibliothèque Adhan
        CalculationParameters parameters = getCalculationParameters(methode);

        // ÉTAPE 3 : Date actuelle
        LocalDate today = LocalDate.now();
        DateComponents dateComponents = new DateComponents(today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        // ÉTAPE 4 : Calcul effectif
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, parameters);

        // ÉTAPE 5 : Formatage des résultats (HH:mm)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        ZoneId zoneId = ZoneId.systemDefault(); // Idéalement, ceci devrait être paramétrable par l'utilisateur

        return HorairesPriereResponse.builder()
                .fajr(formatDate(prayerTimes.fajr, zoneId, formatter))
                .shuruq(formatDate(prayerTimes.sunrise, zoneId, formatter))
                .dhuhr(formatDate(prayerTimes.dhuhr, zoneId, formatter))
                .asr(formatDate(prayerTimes.asr, zoneId, formatter))
                .maghrib(formatDate(prayerTimes.maghrib, zoneId, formatter))
                .isha(formatDate(prayerTimes.isha, zoneId, formatter))
                .date(today.toString())
                .methodeCalcul(methode.name())
                .build();
    }

    /**
     * ÉTAPE 2 : Calculer la direction de la Qibla.
     * @return Angle en degrés par rapport au Nord (sens horaire).
     */
    public double calculerQibla(double latitude, double longitude) {
        Coordinates coordinates = new Coordinates(latitude, longitude);
        return new Qibla(coordinates).direction;
    }

    /**
     * Utilitaire : Mappe notre Enum vers les méthodes de calcul de la bibliothèque Adhan.
     */
    private CalculationParameters getCalculationParameters(MethodeCalcul methode) {
        return switch (methode) {
            case MWL -> CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters();
            case ISNA -> CalculationMethod.NORTH_AMERICA.getParameters();
            case EGYPT -> CalculationMethod.EGYPTIAN.getParameters();
            case MAKKAH -> CalculationMethod.UMM_AL_QURA.getParameters();
            case KARACHI -> CalculationMethod.KARACHI.getParameters();
            case TEHRAN -> {
                // Paramètres pour l'Institut de Géophysique de l'Université de Téhéran
                // Fajr: 17.7°, Isha: 14°
                CalculationParameters params = new CalculationParameters(17.7, 14.0);
                // Note: maghribAngle n'est pas supporté dans cette version de la lib,
                // on ajoute un ajustement de 15 min pour le Maghrib (approximation de 4.5°).
                params.adjustments.maghrib = 15;
                yield params;
            }
            case JAFARI -> {
                // Paramètres pour la méthode Shia Ithna-Ashari (Jafari)
                // Fajr: 16°, Isha: 14°
                CalculationParameters params = new CalculationParameters(16.0, 14.0);
                // Note: maghribAngle n'est pas supporté dans cette version de la lib,
                // on ajoute un ajustement de 15 min pour le Maghrib (approximation de 4°).
                params.adjustments.maghrib = 15;
                yield params;
            }
            default -> CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters();
        };
    }

    /**
     * Utilitaire : Formate une Date java.util vers une chaîne HH:mm.
     */
    private String formatDate(Date date, ZoneId zoneId, DateTimeFormatter formatter) {
        if (date == null) return "--:--";
        return date.toInstant()
                .atZone(zoneId)
                .toLocalTime()
                .format(formatter);
    }
}
