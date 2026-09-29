package progettoUnibs.view;

import java.util.ArrayList;

public class ViewCorpoDati {
    
    private static final String MESSAGGIO_CREARE_CORPO_DATI = "Vuoi creare il corpo dati?";
    private static final String MESSAGGIO_CORPO_DATI_ESISTENTE = "Corpo dati già esistente.";
    private static final String PREFISSO_LUOGHI = "- ";
    private static final String PREFISSO_TIPI_VISITA = "-";
    
    public static boolean chiediSeVuoiCreareCorpoDati() {
        return InputReader.yesOrNo(MESSAGGIO_CREARE_CORPO_DATI);
    }

    public static void mostraCorpoDatiEsistente() {
        System.out.println(MESSAGGIO_CORPO_DATI_ESISTENTE);
    }

    public static void visualizzaNomiLuoghi(ArrayList<String> nomiLuoghi) {
       for (String nome : nomiLuoghi) {
            System.out.println(PREFISSO_LUOGHI + nome);
        }
    }

    public static void visualizzaNomiTipiVisita(ArrayList<String> nomiTipiVisita) {
    for (String nome : nomiTipiVisita) {
            System.out.println(PREFISSO_TIPI_VISITA + nome);
        }
    }
}
