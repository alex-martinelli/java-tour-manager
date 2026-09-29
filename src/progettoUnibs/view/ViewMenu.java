package progettoUnibs.view;

public class ViewMenu {
    private static final String GESTIONE_APPLICAZIONE_CONFIGURATORE = "GESTIONE APPLICAZIONE: CONFIGURATORE";
    private static final String SCELTA_NON_VALIDA = "Scelta non valida";
    private static final String MENU_TITLE = "GESTIONE APPLICAZIONE";
    private static final String CONFIGURATORE_OPTION = "Configuratore";
    private static final String VOLONTARIO_OPTION = "Volontario";
    private static final String USCITA_IN_CORSO = "Uscita in corso...";
    private static final String FRUITORE_OPTION = "Fruitore";
    final private static String CORNICE = "--------------------------------";
    final private static String VOCE_USCITA = "0\tEsci";
    final private static String RICHIESTA_INSERIMENTO = "Digita il numero dell'opzione desiderata > ";
    private static final String MSG_OPERAZIONE_NON_RICONOSCIUTA = "Operazione non riconosciuta.";
    private static final int INCREMENTO_INDICE = 1;
    private static final String SEPARATORE_TAB = "\t";
    private static final int VALORE_MINIMO_SCELTA = 0;
    private static final String DATI_NON_PRESENTI = "Dati non presenti, impossibile procedere.";

   

    public enum MenuOutcome {
        GESTIONE_APPLICAZIONE_CONFIGURATORE,
        CONFIGURATORE, MENU_TITLE, VOLONTARIO_OPTION,
        FRUITORE_OPTION, USCITA_IN_CORSO, SCELTA_NON_VALIDA, DATI_NON_PRESENTI
    }

    public void showResult(MenuOutcome res) {
        switch (res) {
            case GESTIONE_APPLICAZIONE_CONFIGURATORE -> System.out.println(GESTIONE_APPLICAZIONE_CONFIGURATORE);
            case CONFIGURATORE -> System.out.println(CONFIGURATORE_OPTION);
            case MENU_TITLE -> System.out.println(MENU_TITLE);
            case VOLONTARIO_OPTION -> System.out.println(VOLONTARIO_OPTION);
            case FRUITORE_OPTION -> System.out.println(FRUITORE_OPTION);
            case USCITA_IN_CORSO -> System.out.println(USCITA_IN_CORSO);
            case DATI_NON_PRESENTI -> System.out.println(DATI_NON_PRESENTI);
            case SCELTA_NON_VALIDA -> System.out.println(SCELTA_NON_VALIDA);

            default -> System.out.println(MSG_OPERAZIONE_NON_RICONOSCIUTA);
        }
    }

    public int menuView(String titolo, String[] VOCI) {
        return scegli(titolo,VOCI);
    }

    public int scegli(String titolo, String[] voci) {
        stampaMenu(voci,titolo);
        return InputReader.leggiIntero(RICHIESTA_INSERIMENTO, VALORE_MINIMO_SCELTA, voci.length);
    }

    public void stampaMenu(String [] voci,String titolo) {
        System.out.println(CORNICE);
        System.out.println(titolo);
        System.out.println(CORNICE);
        for (int i = 0; i < voci.length; i++) {
            System.out.println((i + INCREMENTO_INDICE) + SEPARATORE_TAB + voci[i]);
        }
        System.out.println();
        System.out.println(VOCE_USCITA);
        System.out.println();
    }

}
