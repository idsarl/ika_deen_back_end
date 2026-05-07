package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sourates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sourate {

    @Id
    private String id;

    @Indexed(unique = true)
    private int numero;

    private String nomArabe;

    private String nomFrancais;

    private String urlAudio;

    @Indexed
    private String recitateurId;
}
