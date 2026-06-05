package ika_deen.back_end.controller;

import ika_deen.back_end.service.QuranSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/quran")
@RequiredArgsConstructor
@Tag(name = "Admin - Coran", description = "Administration du Coran complet")
public class AdminQuranController {

    private final QuranSyncService quranSyncService;

    @PostMapping("/sync")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Synchroniser tout le Coran (SUPER_ADMIN)", description = "Télécharge 114 sourates et 6236 versets depuis Al Quran Cloud. Cette opération peut prendre quelques secondes.")
    public ResponseEntity<Map<String, String>> syncQuran() {
        quranSyncService.syncQuran();
        return ResponseEntity.ok(Map.of("message", "Synchronisation du Coran terminée avec succès."));
    }
}
