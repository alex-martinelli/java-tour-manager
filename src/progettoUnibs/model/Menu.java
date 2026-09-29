package progettoUnibs.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    
    private static final String VOCE_INSERIRE_DATE_PRECLUSE = "Inserire date precluse";
    private static final String VOCE_IMPOSTA_NUMERO_MASSIMO = "Imposta numero massimo di iscritti per prenotazione";
    private static final String VOCE_VISUALIZZA_VOLONTARI = "Visualizza elenco volontari";
    private static final String VOCE_VISUALIZZA_LUOGHI = "Visualizza luoghi visitabili";
    private static final String VOCE_VISUALIZZA_TIPI_VISITA = "Visualizza tipi di visita per ciascun luogo";
    private static final String VOCE_VISUALIZZA_VISITE_STATO = "Visualizza le visite in stato di visita proposta/completa/confermata/cancellata/effettuata";
    private static final String VOCE_CREA_ATTIVITA = "Crea attività";
    private static final String VOCE_AGGIUNTA_DATI = "Aggiunta dati attività";
    private static final String VOCE_RIMOZIONE_DATI = "Rimozione dati attività";
    private static final String VOCE_APRI_RACCOLTA = "Apri raccolta disponibilità volontari";
    private static final int MESI_MINIMI_ATTIVITA = 1;
    private static final int DIMENSIONE_VISITE_VUOTA = 0;

    public static List<String> creaListaVoci(int dimensioneVisite, CorpoDati dati, boolean raccoltaAperta, boolean condModificaAttivita) {
        List<String> vociList = new ArrayList<>();
        vociList.add(VOCE_INSERIRE_DATE_PRECLUSE);
        vociList.add(VOCE_IMPOSTA_NUMERO_MASSIMO);
        vociList.add(VOCE_VISUALIZZA_VOLONTARI);
        vociList.add(VOCE_VISUALIZZA_LUOGHI);
        vociList.add(VOCE_VISUALIZZA_TIPI_VISITA);
        vociList.add(VOCE_VISUALIZZA_VISITE_STATO);
         if (Period.between(dati.getDataAvvioAttivita(), LocalDate.now()).getMonths() >= MESI_MINIMI_ATTIVITA && dimensioneVisite == DIMENSIONE_VISITE_VUOTA) {
            vociList.add(VOCE_CREA_ATTIVITA);
        }
        if(condModificaAttivita) {
            if (!raccoltaAperta) {
                vociList.add(VOCE_AGGIUNTA_DATI);
                vociList.add(VOCE_RIMOZIONE_DATI);
            }
        }
         if (!raccoltaAperta) {
            vociList.add(VOCE_APRI_RACCOLTA);
        }

        return vociList;
    }

  
}
