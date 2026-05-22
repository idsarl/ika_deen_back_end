package ika_deen.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationPendingResponse {
    private String id;
    private String type;
    private Map<String, Object> contenu;
    private Auteur auteur;
    private LocalDateTime date;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Auteur {
        private String id;
        private String email;
        private String nom;
    }
}
