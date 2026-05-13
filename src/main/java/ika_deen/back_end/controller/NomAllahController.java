package ika_deen.back_end.controller;

import ika_deen.back_end.entite.NomAllah;
import ika_deen.back_end.service.NomAllahService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/noms-allah")
@RequiredArgsConstructor
@Tag(name = "99 Noms d'Allah", description = "Endpoints pour récupérer les 99 noms d'Allah")
public class NomAllahController {

    private final NomAllahService service;

    @GetMapping
    @Operation(summary = "Récupérer la liste des 99 noms d'Allah")
    public ResponseEntity<List<NomAllah>> getAll() {
        return ResponseEntity.ok(service.getAllNames());
    }
}
