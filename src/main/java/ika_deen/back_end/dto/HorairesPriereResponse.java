package ika_deen.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour retourner les horaires de prière calculés.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorairesPriereResponse {
    private String fajr;
    private String shuruq; // Lever du soleil
    private String dhuhr;
    private String asr;
    private String maghrib;
    private String isha;
    private String date; // Date concernée
    private String methodeCalcul;
}
