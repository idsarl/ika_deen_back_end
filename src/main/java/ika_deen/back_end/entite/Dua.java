package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "duas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dua {

    @Id
    private String id;

    private String titre;

    private String contenu;

    private String traduction;

    @Indexed
    private String categorie;
}
