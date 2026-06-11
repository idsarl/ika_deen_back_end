package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "evenements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    private String id;

    @Indexed
    private String mosqueeId;

    private String titre;

    private String description;

    private Instant dateEvenement;

    private String imageUrl;

    @CreatedDate
    private LocalDateTime dateCreation;
}
