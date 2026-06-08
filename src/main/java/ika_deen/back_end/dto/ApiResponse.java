package ika_deen.back_end.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Enveloppe de réponse API uniforme pour tous les endpoints")
public class ApiResponse<T> {

    @Schema(description = "Code HTTP de la réponse", example = "200")
    private int code;

    @Schema(description = "Message de la réponse", example = "Succès")
    private String message;

    @Schema(description = "Données du résultat")
    private T data;

    @Schema(description = "Timestamp de la réponse", example = "2026-06-08T10:30:45")
    private LocalDateTime timestamp;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Succès", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Ressource créée avec succès", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, "Pas de contenu", null);
    }
}
