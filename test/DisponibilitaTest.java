import java.time.Month;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.Test;

import progettoUnibs.controller.DisponibilitaFileManager;
import progettoUnibs.model.Disponibilita;

public class DisponibilitaTest {

    private static final String VOLONTARIO_TEST = "beppe";
    private static final int GIORNO_DISPONIBILITA_TEST = 11;
    private static final String ORGANIZZAZIONE_TEST = "testFiles";
    private static final String DIRECTORY_SRC = "src";
    private static final String PREFISSO_FILE_DISPONIBILITA = "disponibilita_";
    private static final String ESTENSIONE_JSON = ".json";

    @Test
    public void testDisponibilita() throws Exception {
        Month mese = LocalDate.now().getMonth();
        Map<String, List<MonthDay>> dateVisite = new HashMap<>();
        dateVisite.put(VOLONTARIO_TEST, List.of(MonthDay.of(mese.getValue(), GIORNO_DISPONIBILITA_TEST)));
        Disponibilita disp = new Disponibilita(false, mese, dateVisite);
        DisponibilitaFileManager disponibilitaFileManager = new DisponibilitaFileManager();

        File file = Path.of(DIRECTORY_SRC, ORGANIZZAZIONE_TEST, PREFISSO_FILE_DISPONIBILITA + mese + ESTENSIONE_JSON).toFile();
        if (file.exists()) {
            file.delete();
        }

        disponibilitaFileManager.salvaDisponibilita(disp, ORGANIZZAZIONE_TEST);

        assertTrue(file.exists());

        String contenuto = Files.readString(file.toPath());

        assertTrue(contenuto.contains(VOLONTARIO_TEST));
    }
}
