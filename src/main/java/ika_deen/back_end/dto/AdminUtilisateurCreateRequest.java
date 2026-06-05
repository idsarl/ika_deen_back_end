package ika_deen.back_end.dto;

import ika_deen.back_end.enumeration.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUtilisateurCreateRequest {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6)
    private String motDePasse;

    private String telephone;

    @Builder.Default
    private Role role = Role.UTILISATEUR;

    @Builder.Default
    private boolean estActif = true;

    @Builder.Default
    private boolean estVerifie = true;

    private String mosqueeId;
}
