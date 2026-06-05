package ika_deen.back_end.service;

import ika_deen.back_end.dto.AlQuranResponse;
import ika_deen.back_end.entite.Sourate;
import ika_deen.back_end.entite.Verset;
import ika_deen.back_end.repository.SourateRepository;
import ika_deen.back_end.repository.VersetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuranSyncService {

    private final SourateRepository sourateRepository;
    private final VersetRepository versetRepository;
    
    // URL format for audio
    private static final String AUDIO_URL_TEMPLATE = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/%d.mp3";

    public void syncQuran() {
        log.info("Début de la synchronisation du Coran depuis Al Quran Cloud...");
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. Fetch Uthmani script (Arabic)
            String uthmaniUrl = "https://api.alquran.cloud/v1/quran/quran-uthmani";
            log.info("Téléchargement du texte arabe Uthmani : {}", uthmaniUrl);
            AlQuranResponse uthmaniResponse = restTemplate.getForObject(uthmaniUrl, AlQuranResponse.class);

            // 2. Fetch French translation (Hamidullah)
            String frenchUrl = "https://api.alquran.cloud/v1/quran/fr.hamidullah";
            log.info("Téléchargement de la traduction française : {}", frenchUrl);
            AlQuranResponse frenchResponse = restTemplate.getForObject(frenchUrl, AlQuranResponse.class);

            if (uthmaniResponse == null || uthmaniResponse.getData() == null || 
                frenchResponse == null || frenchResponse.getData() == null) {
                log.error("Erreur lors de la récupération des données de l'API externe.");
                throw new RuntimeException("API Response is null");
            }

            // 3. Clear existing data to avoid duplicates (or use upsert logic)
            // For a full sync, clearing is usually cleaner
            sourateRepository.deleteAll();
            versetRepository.deleteAll();
            log.info("Anciennes données supprimées de la base de données.");

            List<Sourate> souratesToSave = new ArrayList<>();
            List<Verset> versetsToSave = new ArrayList<>();

            List<AlQuranResponse.SurahDto> arabicSurahs = uthmaniResponse.getData().getSurahs();
            List<AlQuranResponse.SurahDto> frenchSurahs = frenchResponse.getData().getSurahs();

            for (int i = 0; i < arabicSurahs.size(); i++) {
                AlQuranResponse.SurahDto arSurah = arabicSurahs.get(i);
                AlQuranResponse.SurahDto frSurah = frenchSurahs.get(i);

                // Create Sourate entity
                Sourate sourate = Sourate.builder()
                        .numero(arSurah.getNumber())
                        .nomArabe(arSurah.getName())
                        .nomFrancais(frSurah.getEnglishNameTranslation()) // Al Quran Cloud puts french name in englishNameTranslation for french editions
                        .recitateurId("alafasy")
                        .build();
                
                souratesToSave.add(sourate);

                // Create Verset entities
                for (int j = 0; j < arSurah.getAyahs().size(); j++) {
                    AlQuranResponse.AyahDto arAyah = arSurah.getAyahs().get(j);
                    AlQuranResponse.AyahDto frAyah = frSurah.getAyahs().get(j);

                    Verset verset = Verset.builder()
                            .sourateNumero(arSurah.getNumber())
                            .versetNumero(arAyah.getNumberInSurah())
                            .texteArabe(arAyah.getText())
                            .texteFrancais(frAyah.getText())
                            .urlAudio(String.format(AUDIO_URL_TEMPLATE, arAyah.getNumber())) // Global ayah number
                            .build();

                    versetsToSave.add(verset);
                }
            }

            // 4. Save to database
            sourateRepository.saveAll(souratesToSave);
            versetRepository.saveAll(versetsToSave);
            
            log.info("Synchronisation terminée avec succès : {} sourates et {} versets insérés.", souratesToSave.size(), versetsToSave.size());

        } catch (Exception e) {
            log.error("Une erreur est survenue pendant la synchronisation : ", e);
            throw new RuntimeException("Erreur de synchronisation du Coran", e);
        }
    }
}
