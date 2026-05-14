package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "versets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Verset {

    @Id
    private String id;

    @Indexed
    private int sourateNumero;

    private int versetNumero;

    private String texteArabe;

    private String texteFrancais;

    private String urlAudio; // Optionnel : pour écouter le verset seul
}
