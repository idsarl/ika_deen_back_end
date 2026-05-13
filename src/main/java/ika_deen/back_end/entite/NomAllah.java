package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Entité représentant l'un des 99 noms d'Allah.
 */
@Document(collection = "noms_allah")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomAllah {
    @Id
    private String id;
    private int numero;
    private String arabe;
    private String translitteration;
    private Map<String, String> signification; // ex: {"fr": "Le Tout-Miséricordieux", "en": "The Most Merciful"}
}
