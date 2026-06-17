package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "horaires_villes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorairesVille {
    @Id
    private String id;

    @Indexed(unique = true)
    private String nomVille; // ex: "Bamako", "Sikasso"

    private String fajr;
    private String dhuhr;
    private String asr;
    private String maghrib;
    private String isha;

    private LocalDate dateValidite; // Pour savoir quand ces horaires ont été fixés
    private String sourceAutorite; // ex: "Imamat Général du Mali"
}
