package ika_deen.back_end.dto;

import ika_deen.back_end.enumeration.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUtilisateurUpdateRequest {

    @Email
    private String email;

    @Size(min = 6)
    private String motDePasse;

    private String telephone;
    private Role role;
    private Boolean estActif;
    private Boolean estVerifie;
    private String mosqueeId;
}
