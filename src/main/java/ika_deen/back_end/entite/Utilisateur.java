package ika_deen.back_end.entite;

import ika_deen.back_end.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "utilisateurs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String motDePasseHash;

    private String telephone;

    private Role role;

    @Builder.Default
    private java.util.List<String> mosqueeIds = new java.util.ArrayList<>();

    @Builder.Default
    private boolean estActif = true;

    @Builder.Default
    private boolean estVerifie = false;

    private String tokenVerification;

    private String tokenReinitialisation;

    private LocalDateTime dateExpirationToken;

    private String tokenFcm;

    @CreatedDate
    private LocalDateTime dateCreation;

    @LastModifiedDate
    private LocalDateTime dateMiseAJour;

    private LocalDateTime dateDerniereConnexion;
}
