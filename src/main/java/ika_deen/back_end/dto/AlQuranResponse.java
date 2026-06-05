package ika_deen.back_end.dto;

import lombok.Data;
import java.util.List;

@Data
public class AlQuranResponse {
    private int code;
    private String status;
    private AlQuranData data;

    @Data
    public static class AlQuranData {
        private List<SurahDto> surahs;
    }

    @Data
    public static class SurahDto {
        private int number;
        private String name;
        private String englishName;
        private String englishNameTranslation;
        private String revelationType;
        private List<AyahDto> ayahs;
    }

    @Data
    public static class AyahDto {
        private int number;
        private String text;
        private int numberInSurah;
        private int juz;
        private int manzil;
        private int page;
        private int ruku;
        private int hizbQuarter;
        private Object sajda;
    }
}
