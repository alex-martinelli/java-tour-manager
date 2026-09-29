import static org.junit.Assert.*;

import org.junit.Test;

import progettoUnibs.controller.CredenzialiFileManager;

public class EsisteUsernameGlobaleTest {

    private static final String USERNAME_VALIDO_TEST = "marco";
    private static final String USERNAME_NON_VALIDO_TEST = "aaa";

    private CredenzialiFileManager gestioneCredenziali = new CredenzialiFileManager();

    @Test
    public void testEsisteUsernameGlobale() {
        assertTrue(gestioneCredenziali.esisteUsernameGlobale(USERNAME_VALIDO_TEST));
        assertFalse(gestioneCredenziali.esisteUsernameGlobale(USERNAME_NON_VALIDO_TEST));
    }
}
