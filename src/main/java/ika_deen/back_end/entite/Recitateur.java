package ika_deen.back_end.entite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recitateurs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recitateur {

    @Id
    private String id;

    private String nom;

    private String photoUrl;
}
