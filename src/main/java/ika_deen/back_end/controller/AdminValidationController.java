package ika_deen.back_end.controller;

import ika_deen.back_end.dto.ValidationPendingResponse;
import ika_deen.back_end.dto.ValidationRejectRequest;
import ika_deen.back_end.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/validations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin - Validations", description = "Modération des contenus en attente")
public class AdminValidationController {

    private final ValidationService validationService;

    @GetMapping("/pending")
    @Operation(summary = "Lister les validations en attente")
    public ResponseEntity<List<ValidationPendingResponse>> getPending() {
        return ResponseEntity.ok(validationService.getPending());
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approuver une validation")
    public ResponseEntity<ValidationPendingResponse> approve(@PathVariable String id) {
        return ResponseEntity.ok(validationService.approve(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Rejeter une validation")
    public ResponseEntity<ValidationPendingResponse> reject(
            @PathVariable String id,
            @RequestBody(required = false) ValidationRejectRequest request) {
        String motif = request != null ? request.getMotif() : null;
        return ResponseEntity.ok(validationService.reject(id, motif));
    }
}
