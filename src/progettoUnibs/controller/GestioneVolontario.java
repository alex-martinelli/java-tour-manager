package progettoUnibs.controller;

import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Disponibilita;
import progettoUnibs.model.Prenotazione;
import progettoUnibs.model.StatoConfermata;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.model.Visita;
import progettoUnibs.model.Volontario;

import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.OutputVisita;
import progettoUnibs.view.InputVisita;

public class GestioneVolontario {
    
    private static final String MSG_NESSUNA_VISITA_TROVATA_VOLONTARIO = "Nessuna visita trovata per il volontario ";
    private static final String MSG_DATE_DISPONIBILI = "Date disponibili per \"";
    private static final String MSG_NESSUNA_VISITA_TROVATA_RIPROVA = "Nessuna visita trovata con quel titolo e quella data. Riprova";
    private static final String MSG_ELENCO_PRENOTAZIONI = "\n ELENCO PRENOTAZIONI:";
    private static final String MSG_VISITE_PER = "Visite per ";
    private static final String MSG_NESSUNA_VISITA_TROVATA_PER = "Nessuna visita trovata per ";
    
    private static final String SEPARATORE_PRENOTAZIONI = "----------------------- \n";
    private static final String FORMATO_PRENOTAZIONE = "codice: %d\n numero persone: %d";
    private static final String SEPARATORE_VIRGOLA = ", ";
    private static final String SEPARATORE_DUE_PUNTI = ": ";
    private static final String CHIUSURA_VIRGOLETTE = "\":";

    private InputVisita inputVisita = new InputVisita();
    private OutputVisita visitaView = new OutputVisita();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneAttivita gestioneAttivita;
    private GestioneCorpoDati gestioneCorpoDati;
    private RepositoryManager repositoryManager;

    public GestioneVolontario(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneAttivita(new GestioneAttivita(repositoryManager));
        setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));
    }

    public void setGestioneAttivita(GestioneAttivita gestioneAtt) {
        gestioneAttivita = gestioneAtt;
    }

    public void setGestioneCorpoDati(GestioneCorpoDati gestioneCorpo) {
        gestioneCorpoDati = gestioneCorpo;
    }

    public void visualizzaConvocazioni(Volontario volontario) {
        String org = volontario.getOrganizzazione();
        ArrayList<Visita> visite = gestioneAttivita.caricaVisite(org, new StatoConfermata());
        if (visite.isEmpty()) {
            outputPrinter.visualizzaMessaggio(MSG_NESSUNA_VISITA_TROVATA_VOLONTARIO + volontario.getCredenziali().getUsername());
            return;
        }
        for (Visita v : visite) {
            if (v.getVolontario().equalsIgnoreCase(volontario.getCredenziali().getUsername())) {
                visitaView.stampaVisitaView(v);
            }
        }
    }

    public void visualizzaPrenotazioniVisita(Volontario volontario) {
        visualizzaConvocazioni(volontario);
        String org = volontario.getOrganizzazione();
        ArrayList<Visita> visite = gestioneAttivita.caricaVisite(org, new StatoConfermata());
        Visita visita = null;
        if (visite.isEmpty()) {
            return;
        }
        boolean valido = false;
        do {
            String nomeVisita = inputVisita.askNomeVisita();
            List<Visita> visiteConTitolo = visite.stream()
                    .filter(v -> v.getTipoVisita().getTitolo().equalsIgnoreCase(nomeVisita)
                            && v.getVolontario().equalsIgnoreCase(volontario.getCredenziali().getUsername()))
                    .collect(Collectors.toList());
            outputPrinter.visualizzaMessaggio(MSG_DATE_DISPONIBILI + nomeVisita + CHIUSURA_VIRGOLETTE);
            visiteConTitolo.forEach(v -> outputPrinter.visualizzaMessaggio("" + v.getData()));
            MonthDay dataVisita = inputVisita.askDataVisita();
            for (Visita v : visiteConTitolo) {
                if (v.getData().equals(dataVisita)) {
                    visita = v;
                    valido = true;
                    break;
                }
            }
            if (!valido) {
                outputPrinter.visualizzaMessaggio(MSG_NESSUNA_VISITA_TROVATA_RIPROVA);
            }
        } while (!valido);
        outputPrinter.visualizzaMessaggio(MSG_ELENCO_PRENOTAZIONI);
        for (Prenotazione p : visita.getPrenotazioni()) {
            outputPrinter.visualizzaMessaggio(SEPARATORE_PRENOTAZIONI + String.format(FORMATO_PRENOTAZIONE, p.getCodice(), p.getNumeroPersone()));
        }
    }

    public void visualizzaVisitePerVolontario(String nomeVolontario, CorpoDati dati) {
        gestioneCorpoDati.setDati(dati);
        Map<String, List<String>> volontariMap = repositoryManager.getVolontariRepository().costruisciMappaVolontari(dati);
        if (volontariMap.containsKey(nomeVolontario)) {
            outputPrinter.visualizzaMessaggio(MSG_VISITE_PER + nomeVolontario + SEPARATORE_DUE_PUNTI + String.join(SEPARATORE_VIRGOLA, volontariMap.get(nomeVolontario)));
        } else {
            outputPrinter.visualizzaMessaggio(MSG_NESSUNA_VISITA_TROVATA_PER + nomeVolontario);
        }
    }

    public String volontarioDisponibile(TipoVisita tipo, String organizzazione, MonthDay giorno) {
        for (String v : tipo.getVolontari()) {
            if (isVolontarioDisponibile(v, giorno, organizzazione)) {
                return v;
            }
        }
        return null;
    }

    public boolean esisteVolontarioDisponibile(TipoVisita tipo, String organizzazione, MonthDay giorno) {
        for (String v : tipo.getVolontari()) {
            if (isVolontarioDisponibile(v, giorno, organizzazione)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVolontarioDisponibile(String volontario, MonthDay giorno, String organizzazione) {
        Month mese = giorno.getMonth();
        Disponibilita disponibilita = repositoryManager.getDisponibilitaRepository().leggiDisponibilita(mese, organizzazione);
        if (disponibilita == null) {
            return false;
        }
        return disponibilita.getDisponibilitaMap()
                .containsKey(volontario)
                && disponibilita.getDisponibilitaMap().get(volontario).contains(giorno);
    }

    public boolean volontarioOccupato(String nickname, String organizzazione, TipoVisita tipo) {
        CorpoDati dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(organizzazione);
        if (dati == null)
            return false;

        return tipo.volontarioOccupatoPerTipoVisita(dati, nickname, organizzazione);
    }
}