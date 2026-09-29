import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import progettoUnibs.controller.CredenzialiFileManager;
import progettoUnibs.controller.CredenzialiRepository;

public class CaricaConfDaFileTest {

    private static final String ORGANIZZAZIONE_TEST = "testFiles";
    private static final int DIMENSIONE_MAPPA_ATTESA = 2;
    private static final String PASSWORD_TEST = "1234";
    private static final String USERNAME_TEST = "alice";

    @Test
    public void testCaricaCredenzialiConfDaFile() {

        CredenzialiRepository repo = new CredenzialiFileManager();
        Map<String, String> mappa = repo.caricaCredenzialiConfDaFile(ORGANIZZAZIONE_TEST);

        assertEquals(DIMENSIONE_MAPPA_ATTESA, mappa.size());
        assertEquals(PASSWORD_TEST, mappa.get(USERNAME_TEST));
    }
}
