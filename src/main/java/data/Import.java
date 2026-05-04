package data;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

/**
 * Classe Import
 *
 * Gère l'importation et le formatage des données des musées
 */
public class Import {

    private static final String[] MUSEUM_FIELDS = {"identifiant", "nom_officiel", "adresse", "code_postal", "ville", "region", "departement", "domaine_thematique", "histoire", "atout", "interet", "annee_creation"};
    private static final String[] MUSEUM_OUTPUT_FIELDS = Arrays.copyOfRange(MUSEUM_FIELDS, 1, MUSEUM_FIELDS.length);
    private static final String[] VISITORS_FIELDS = {"idmuseofile", "payant", "gratuit", "total", "individuel", "scolaires", "groupes_hors_scolaires", "moins_18_ans_hors_scolaires", "_18_25_ans"};
    private static final String[] FINAL_FIELDS = Stream.concat(Arrays.stream(MUSEUM_OUTPUT_FIELDS), Arrays.stream(VISITORS_FIELDS)).toArray(String[]::new);
    private static final String MAIN_DATA = "data/musees-de-france-base-museofile.csv";
    private static final String VISITORS_DATA = "data/ENTREES_ET_CATEGORIES_DE_PUBLIC.csv";
    private static final String OUTPUT_DATA = "data/formatted_museums.csv";

    // Convertit une valeur en chaîne d'entier
    private static String toIntegerString(String value, String fallback) {
        if (value == null) {
            return fallback;
        }

        String normalized = value.trim()
                .replace("\u00A0", "")
                .replace(" ", "")
                .replace(',', '.');

        if (normalized.isEmpty()) {
            return fallback;
        }

        try {
            return String.valueOf((int) Math.round(Double.parseDouble(normalized)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static void formatGlobal() {
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').withIgnoreQuotations(true).withStrictQuotes(false)
                    .withIgnoreLeadingWhiteSpace(true).build();

            //MUSEUMS
            CSVReader readerMain = new CSVReaderBuilder(new FileReader(MAIN_DATA)).withCSVParser(parser).build();
            List<String[]> museums = new ArrayList<>();
            String[] headersMain = readerMain.readNext();

            Map<String, Integer> headerIndexMain = new HashMap<>();

            for (int i = 0; i < headersMain.length; i++) {
                String normalized = headersMain[i].trim().toLowerCase();
                if (i == 0) {
                    normalized = normalized.replace("\uFEFF", "");
                }
                normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                headerIndexMain.put(normalized, i);
            }

            // Filtrage des données pour ne garder que les champs nécessaires
            String[] lineMain;
            while ((lineMain = readerMain.readNext()) != null) {
                if (lineMain.length < headersMain.length) {
                    continue;
                }
                if ("".equals(lineMain[headerIndexMain.get("domaine_thematique")])) {
                    continue;
                }

                String[] filteredLine = new String[MUSEUM_FIELDS.length];

                for (int i = 0; i < MUSEUM_FIELDS.length; i++) {
                    filteredLine[i] = lineMain[headerIndexMain.get(MUSEUM_FIELDS[i])];
                    if (filteredLine[i] == null) {
                        filteredLine[i] = "";
                    }
                }

                filteredLine[11] = toIntegerString(filteredLine[11], "");

                museums.add(filteredLine);
            }
            readerMain.close();

            CSVReader readerVisitors = new CSVReaderBuilder(new FileReader(VISITORS_DATA)).withCSVParser(parser).build();
            List<String[]> visitors = new ArrayList<>();
            String[] headersVisitors = readerVisitors.readNext();

            Map<String, Integer> headerIndexVisitors = new HashMap<>();

            for (int i = 0; i < headersVisitors.length; i++) {
                String normalized = headersVisitors[i].trim().toLowerCase();
                if (i == 0) {
                    normalized = normalized.replace("\uFEFF", "");
                }
                normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                headerIndexVisitors.put(normalized, i);
            }

            String[] lineVisitors;
            while ((lineVisitors = readerVisitors.readNext()) != null) {
                try {

                    if (lineVisitors[headerIndexVisitors.get("total")] == null || Integer.parseInt(lineVisitors[headerIndexVisitors.get("total")]) == 0) {
                        continue;
                    }
                    if (Integer.parseInt(lineVisitors[headerIndexVisitors.get("annee")]) != 2023) {
                        continue;
                    }

                    String[] filteredLine = new String[VISITORS_FIELDS.length];
                    for (int i = 0; i < VISITORS_FIELDS.length; i++) {
                        filteredLine[i] = lineVisitors[headerIndexVisitors.get(VISITORS_FIELDS[i])];
                        if (filteredLine[i] == null) {
                            filteredLine[i] = "";
                        }
                    }

                    for (int i = 1; i < filteredLine.length; i++) {
                        filteredLine[i] = toIntegerString(filteredLine[i], "");
                    }

                    visitors.add(filteredLine);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
            readerVisitors.close();

            List<String[]> finalMuseums = new ArrayList<>();
            for (String[] museum : museums) {
                String id = museum[0];
                String[] visitorData = visitors.stream().filter(v -> v[0].equals(id)).findFirst().orElse(null);
                if (visitorData != null) {
                    String[] museumWithoutId = Arrays.copyOfRange(museum, 1, museum.length);
                    finalMuseums.add(Stream.concat(Arrays.stream(museumWithoutId), Arrays.stream(visitorData)).toArray(String[]::new));

                }

            }

            CSVWriter writer = new CSVWriter(new FileWriter(OUTPUT_DATA));
            writer.writeNext(FINAL_FIELDS, false);
            for (String[] museum : finalMuseums) {
                writer.writeNext(museum, false);
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Main {

    public static void main(String[] args) {
        Import.formatGlobal();
    }
}
