package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "publicites")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publicite {

    @Id
    private String id;

    private String imageUrl;

    private String lienDestination;

    @Builder.Default
    private boolean estActive = true;

    private LocalDateTime dateFin;

    @CreatedDate
    private LocalDateTime dateCreation;
}
