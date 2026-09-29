import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import progettoUnibs.controller.CredenzialiFileManager;
import progettoUnibs.controller.CredenzialiRepository;
import progettoUnibs.model.TipoUtente;

public class CredenzialiRepositoryTest {

    private static final String ORGANIZZAZIONE_TEST = "testFiles";
    private static final String USERNAME_VECCHIO = "lollo";
    private static final String USERNAME_NUOVO = "bob";
    private static final String PASSWORD_NUOVA = "9090";
    private static final String PASSWORD_VECCHIA = "0000";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private CredenzialiRepository repo;

    @Before
    public void setUp() throws IOException {
        repo = new CredenzialiFileManager();
    }

    @Test
    public void testModificaCredenziali() throws IOException {
        repo.modificaCredenziali(ORGANIZZAZIONE_TEST, USERNAME_VECCHIO, USERNAME_NUOVO, PASSWORD_NUOVA, TipoUtente.CONFIGURATORE);

        Map<String, String> mappa = repo.caricaCredenzialiConfDaFile(ORGANIZZAZIONE_TEST);
        assertEquals(PASSWORD_NUOVA, mappa.get(USERNAME_NUOVO));
        assertFalse(mappa.containsValue(PASSWORD_VECCHIA));

        repo.modificaCredenziali(ORGANIZZAZIONE_TEST, USERNAME_NUOVO, USERNAME_VECCHIO, PASSWORD_VECCHIA, TipoUtente.CONFIGURATORE);
    }
}