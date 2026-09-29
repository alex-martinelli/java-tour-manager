
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import progettoUnibs.controller.GestioneLuogo;

class GestioneLuogoTest {

    private static final String INDIRIZZO_VALIDO_TEST = "Milano,Via Roma,10";
    private static final String AMBITO_MILANO_TEST = "Milano";
    private static final String INDIRIZZO_NUMERO_NON_VALIDO_TEST = "Milano,Via Roma,dieci";
    private static final String INDIRIZZO_COMUNE_DIVERSO_TEST = "Napoli,Via Roma,10";
    private static final String INDIRIZZO_SOLO_COMUNE_TEST = "SoloComune";
    private static final String INDIRIZZO_SENZA_NUMERO_TEST = "Comune,Via";
    private static final String INDIRIZZO_TROPPI_CAMPI_TEST = "Comune,Via,Numero,Altro";
    private static final String STRINGA_VUOTA_TEST = "";

    static class TestableGestioneLuogo extends GestioneLuogo {
        public TestableGestioneLuogo() {
            super(null);
        }

        public boolean testCollocazioneGeografica(String indirizzo, String ambito) {
            return super.collocazioneGeografica(indirizzo, ambito);
        }
    }

    @Test
    void testCollocazioneGeografica_Valido() {
        TestableGestioneLuogo gestioneLuogo = new TestableGestioneLuogo();

        String indirizzo = INDIRIZZO_VALIDO_TEST;
        String ambito = AMBITO_MILANO_TEST;

        assertFalse(gestioneLuogo.testCollocazioneGeografica(null, ambito));
        assertFalse(gestioneLuogo.testCollocazioneGeografica(STRINGA_VUOTA_TEST, ambito));
        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo, null));
        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo, STRINGA_VUOTA_TEST));
        assertTrue(gestioneLuogo.testCollocazioneGeografica(indirizzo, ambito));
    }

    @Test
    void testCollocazioneGeografica_NumeroCivicoNonValido() {
        TestableGestioneLuogo gestioneLuogo = new TestableGestioneLuogo();

        String indirizzo = INDIRIZZO_NUMERO_NON_VALIDO_TEST;
        String ambito = AMBITO_MILANO_TEST;

        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo, ambito));
    }

    @Test
    void testCollocazioneGeografica_ComuneDiverso() {
        TestableGestioneLuogo gestioneLuogo = new TestableGestioneLuogo();

        String indirizzo = INDIRIZZO_COMUNE_DIVERSO_TEST;
        String ambito = AMBITO_MILANO_TEST;

        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo, ambito));
    }

    @Test
    void testCollocazioneGeografica_FormatoErrato() {
        TestableGestioneLuogo gestioneLuogo = new TestableGestioneLuogo();

        String indirizzo1 = INDIRIZZO_SOLO_COMUNE_TEST;
        String indirizzo2 = INDIRIZZO_SENZA_NUMERO_TEST;
        String indirizzo3 = INDIRIZZO_TROPPI_CAMPI_TEST;

        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo1, AMBITO_MILANO_TEST));
        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo2, AMBITO_MILANO_TEST));
        assertFalse(gestioneLuogo.testCollocazioneGeografica(indirizzo3, AMBITO_MILANO_TEST));
    }
}
