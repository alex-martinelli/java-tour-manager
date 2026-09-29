package progettoUnibs.controller;
import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import progettoUnibs.model.Attivita;
import progettoUnibs.model.Prenotazione;
import progettoUnibs.model.StatoProposta;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.model.Visita;
import progettoUnibs.utils.DateUtils;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;

public class GestionePrenotazioni {

    private static final String MSG_NESSUNA_VISITA_PROPOSTA = "Non ci sono visite proposte al momento.";
    private static final String PROMPT_NOME_VISITA = "Inserisci il nome della visita a cui vuoi iscriverti: ";
    private static final String MSG_TITOLO_NON_VALIDO = "Titolo non valido. Riprova.";
    private static final String MSG_DATE_DISPONIBILI = "Date disponibili per \"";
    private static final String PROMPT_DATA_VISITA = "Inserisci la data della visita (--MM-DD): ";
    private static final String MSG_NESSUNA_VISITA_TROVATA = "Nessuna visita trovata con quel titolo e quella data. Riprova.";
    private static final String PROMPT_NUMERO_PARTECIPANTI = "Inserisci il numero di partecipanti: ";
    private static final String MSG_ISCRIZIONE_SUCCESSO = "Iscrizione effettuata con successo!";
    private static final String PROMPT_CODICE_PRENOTAZIONE = "Inserisci il codice della prenotazione da disdire: ";
    private static final String MSG_PRENOTAZIONE_DISDETTA = "Prenotazione disdetta con successo!";
    private static final String MSG_CODICE_NON_VALIDO = "Codice prenotazione non valido o non associato a questo fruitore.";
    
    private static final String BIGLIETTO_NO = "no";
    private static final String BIGLIETTO_SI = "si";
    private static final String STATO_PROPOSTA = "PROPOSTA";
    private static final String STATO_COMPLETA = "COMPLETA";
    
    private static final String FORMATO_PRENOTAZIONE = "\n\n Prenotazione per %s con codice %d per %d persone\n per la visita: \n titolo:%s\n descrizione: %s\n punto incontro:%s\n data: %s\n ora inizio:%s\n biglietto necessario:%s\n stato:%s";
    
    private static final int INDICE_INIZIO = 0;
    private static final int INDICE_FINE = 1;
    private static final int MIN_PARTECIPANTI = 1;

    private InputReader inputReader = new InputReader();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private RepositoryManager repositoryManager;
    private GestioneAttivita gestioneAttivita;

    public GestionePrenotazioni(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneAttivita(new GestioneAttivita(repositoryManager));
    }

    public void setGestioneAttivita(GestioneAttivita gestioneAtt) {
        gestioneAttivita = gestioneAtt;
    }

    public Visita trovaVisitaDaCodicePrenotazione(int codicePrenotazione, String org) {
        Visita v = null;
        for (int i = INDICE_INIZIO; i <= INDICE_FINE; i++) {
            Month mese = DateUtils.getMeseCorretto(i);
            Attivita attivita = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(org, mese);
            v = attivita.trovaVisitaDaCodicePrenotazioneInAttivita(codicePrenotazione);
            if(v != null) {
                return v;
            }
        }
        return null;
    }

    public void prenotaVisita(String fruitore, String org) {
        ArrayList<Visita> visiteProposte = gestioneAttivita.caricaVisite(org, new StatoProposta());
        if (visiteProposte.isEmpty()) {
            outputPrinter.visualizzaMessaggio(MSG_NESSUNA_VISITA_PROPOSTA);
            return;
        }

        Visita visita = null;
        boolean valido = false;

        do {
            String nomeVisita = InputReader.leggiStringaNonVuota(PROMPT_NOME_VISITA);
            List<Visita> visiteConTitolo = visiteProposte.stream()
                    .filter(v -> v.getTipoVisita().getTitolo().equalsIgnoreCase(nomeVisita))
                    .collect(Collectors.toList());

            if (visiteConTitolo.isEmpty()) {
                outputPrinter.visualizzaMessaggio(MSG_TITOLO_NON_VALIDO);
                continue;
            }
            outputPrinter.visualizzaMessaggio(MSG_DATE_DISPONIBILI + nomeVisita + "\":");
            visiteConTitolo.forEach(v -> outputPrinter.visualizzaMessaggio("" + v.getData()));
            MonthDay dataVisita = inputReader.leggiMonthDay(PROMPT_DATA_VISITA);
            for (Visita v : visiteConTitolo) {
                if (v.getData().equals(dataVisita)) {
                    visita = v;
                    valido = true;
                    break;
                }
            }
            if (!valido) {
                outputPrinter.visualizzaMessaggio(MSG_NESSUNA_VISITA_TROVATA);
            }

        } while (!valido);
        int maxPersonePerIscrizione = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(org).getMaxPersonePerIscrizione();

        int maxIscrizioni = visita.calcolaMaxIscrizioni(maxPersonePerIscrizione);
        int numeroIscrizioni = InputReader.leggiIntero(PROMPT_NUMERO_PARTECIPANTI, MIN_PARTECIPANTI, maxIscrizioni);
        Prenotazione prenotazione = repositoryManager.getPrenotazioniRepository().eseguiIscrizione(visita, org, fruitore, numeroIscrizioni);
        repositoryManager.getAttivitaRepository().aggiornaVisitaInAttivita(prenotazione, visita, org);
        outputPrinter.visualizzaMessaggio(MSG_ISCRIZIONE_SUCCESSO);
    }

    public void visualizzaPrenotazioniAttive(String fruitore, String org) {
        ArrayList<Prenotazione> prenotazioni = repositoryManager.getPrenotazioniRepository().leggiPrenotazioniDaFile(org);
        for (Prenotazione p : prenotazioni) {
            if (p.getNomeFruitore().equalsIgnoreCase(fruitore)) {
                Visita v = trovaVisitaDaCodicePrenotazione(p.getCodice(), org);
                TipoVisita tv = v.getTipoVisita();
                String titolo = v.getTipoVisita().getTitolo();
                String data = v.getData().toString();
                String descrizione = tv.getDescrizione();
                String puntoIncontro = tv.getPuntoIncontro();
                String oraInizio = tv.getOraInizio().toString();
                String biglietto = (tv.getBigliettoNecessario() == false) ? BIGLIETTO_NO : BIGLIETTO_SI;
                String stato = v.getStato().getNome();

                outputPrinter.visualizzaMessaggio(String.format(FORMATO_PRENOTAZIONE,
                        p.getNomeFruitore(), p.getCodice(), p.getNumeroPersone(), titolo, descrizione,
                        puntoIncontro, data, oraInizio, biglietto, stato));
            }
        }
    }

    public void disdiciPrenotazione(String fruitore, String org) {
        visualizzaPrenotazioniVisiteProposte(fruitore, org);
        int codicePrenotazione = InputReader.leggiIntero(PROMPT_CODICE_PRENOTAZIONE);
        ArrayList<Prenotazione> prenotazioni = repositoryManager.getPrenotazioniRepository().leggiPrenotazioniDaFile(org);
        if (Prenotazione.checkCodiceFruitore(codicePrenotazione, org, fruitore, prenotazioni)) {
            repositoryManager.getPrenotazioniRepository().rimuoviPrenotazioneDaFile(codicePrenotazione, fruitore, org);
            Visita visita = trovaVisitaDaCodicePrenotazione(codicePrenotazione, org);
            repositoryManager.getAttivitaRepository().rimuoviPrenotazioneDaVisita(codicePrenotazione, fruitore, org, visita);
            outputPrinter.visualizzaMessaggio(MSG_PRENOTAZIONE_DISDETTA);
        } else {
            outputPrinter.visualizzaMessaggio(MSG_CODICE_NON_VALIDO);
        }
    }

    private void visualizzaPrenotazioniVisiteProposte(String fruitore, String org) {
        ArrayList<Prenotazione> prenotazioni = repositoryManager.getPrenotazioniRepository().leggiPrenotazioniDaFile(org);
        for (Prenotazione p : prenotazioni) {
            if (p.getNomeFruitore().equalsIgnoreCase(fruitore)) {
                Visita v = trovaVisitaDaCodicePrenotazione(p.getCodice(), org);
                TipoVisita tv = v.getTipoVisita();
                String titolo = v.getTipoVisita().getTitolo();
                String data = v.getData().toString();
                String descrizione = tv.getDescrizione();
                String puntoIncontro = tv.getPuntoIncontro();
                String oraInizio = tv.getOraInizio().toString();
                String biglietto = (tv.getBigliettoNecessario() == false) ? BIGLIETTO_NO : BIGLIETTO_SI;
                String stato = v.getStato().getNome();
                if (stato.equalsIgnoreCase(STATO_PROPOSTA) || stato.equalsIgnoreCase(STATO_COMPLETA)) {
                    outputPrinter.visualizzaMessaggio(String.format(FORMATO_PRENOTAZIONE,
                            p.getNomeFruitore(), p.getCodice(), p.getNumeroPersone(), titolo, descrizione,
                            puntoIncontro, data, oraInizio, biglietto, stato));
                }
            }

        }
    }

}
