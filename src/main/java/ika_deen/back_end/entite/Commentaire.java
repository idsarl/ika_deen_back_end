package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "commentaires")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {

    @Id
    private String id;

    @Indexed
    private String mosqueeId;

    @Indexed
    private String utilisateurId;

    private int note; // 1 à 5

    private String contenu;

    @CreatedDate
    private LocalDateTime dateCreation;
}
