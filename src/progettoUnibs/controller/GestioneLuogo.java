package progettoUnibs.controller;

import java.util.ArrayList;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Luogo;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;

public class GestioneLuogo {

    private static final String INSERISCI_DESCRIZIONE_LUOGO = "Inserisci la descrizione del luogo: \n";
    private static final String INSERISCI_INDIRIZZO_LUOGO = "Inserisci l'indirizzo del luogo: (scritto con Comune,Via,NumeroCivico)\n";
    private static final String INDIRIZZO_VALIDO = "Indirizzo valido!";
    private static final String INSERISCI_NOME_IDENTIFICATIVO_LUOGO = "Inserisci il nome identificativo del luogo: \n";
    private static final String DEVI_AGGIUNGERE_ALMENO_UN_TIPO_VISITA = "Devi aggiungere almeno un tipo di visita!";
    private static final String VUOI_AGGIUNGERE_ALTRI_TIPI_VISITA = "Vuoi aggiungere altri tipi di visita?";
    private static final String INDIRIZZO_NON_VALIDO = "Indirizzo non valido!";
    private static final String ERRORE_NUMERO_CIVICO_NON_VALIDO = "Errore: Il Numero Civico deve essere un numero valido.";
    private static final String HAI_INSERITO_AMBITO_ERRATO = "Hai inserito un ambito errato";
    private static final String NOME_GIA_ESISTENTE = "Nome già esistente!";
    private static final String INSERISCI_NOME_LUOGO = "Inserisci nome luogo: ";
    private static final String NESSUN_LUOGO_CON_NOME = "Nessun luogo trovato con il nome: ";
    private static final String VIRGOLA_SEPARATORE = ",";
    private static final int INDICE_NUMERO_CIVICO = 2;
    private static final int LUNGHEZZA_MINIMA_INDIRIZZO = 3;

    private RepositoryManager repositoryManager;
    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneTipoVisita gestioneTipoVisita;

    public GestioneLuogo(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void setGestioneTipoVisita(GestioneTipoVisita gestioneTV) {
        gestioneTipoVisita = gestioneTV;
    }

    public void creazioneLuogo(CorpoDati corpo, ArrayList<Luogo> luoghi) {
        // 1. Crea il luogo
        String nomeIdentificativo = leggiNomeUnivoco(corpo);
        String descrizione = InputReader.leggiStringa(INSERISCI_DESCRIZIONE_LUOGO);
        String indirizzo = leggiIndirizzoValido(corpo);
        Luogo l = new Luogo(nomeIdentificativo, descrizione, indirizzo, new ArrayList<>());
        setGestioneTipoVisita(new GestioneTipoVisita(repositoryManager));

        // 2. Aggiungilo subito alla lista
        luoghi.add(l);
        corpo.setLuoghi(luoghi); // Assicuriamoci che anche il corpo abbia i luoghi aggiornati

        // 3. Imposta data di avvio attività se necessario
        if (repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(corpo.getOrganizzazione()) == null) {
            corpo.impostaDataAvvioAttivita();
        }

        // 4. Salva il file ORA, subito dopo l'aggiunta
        repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(corpo);

        boolean continua;
        do {
            TipoVisita tipoVisita = gestioneTipoVisita.creaTipoVisita(corpo.getOrganizzazione());
            corpo.aggiuntaTipoVisitaaLuogo(luoghi, l, tipoVisita);

            // 6. Salva subito dopo l’aggiunta della visita
            repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(corpo);

            if (l.getTipiVisita().isEmpty()) {
                continua = true;
                outputPrinter.visualizzaMessaggio(DEVI_AGGIUNGERE_ALMENO_UN_TIPO_VISITA);
            } else {
                continua = InputReader.yesOrNo(VUOI_AGGIUNGERE_ALTRI_TIPI_VISITA);
            }
        } while (continua);
    }

    private String leggiIndirizzoValido(CorpoDati corpo) {
        String indirizzo;

        do {
            indirizzo = InputReader.leggiStringaNonVuota(INSERISCI_INDIRIZZO_LUOGO);
        } while (!collocazioneGeografica(indirizzo, corpo.getAmbitoTerritoriale()));

        outputPrinter.visualizzaMessaggio(INDIRIZZO_VALIDO);
        return indirizzo;
    }

    private String leggiNomeUnivoco(CorpoDati corpo) {
        String nomeIdentificativo;

        do {
            nomeIdentificativo = InputReader.leggiStringaNonVuota(INSERISCI_NOME_IDENTIFICATIVO_LUOGO);
            if (corpo.getLuoghi() == null)
                break;
            if (corpo.esisteLuogo(nomeIdentificativo))
                outputPrinter.visualizzaMessaggio(NOME_GIA_ESISTENTE);
        } while (corpo.esisteLuogo(nomeIdentificativo));

        return nomeIdentificativo;
    }

    public boolean collocazioneGeografica(String indirizzo, String ambito) {
        if(indirizzo == null || ambito == null || indirizzo.isEmpty() || ambito.isEmpty()) {
            outputPrinter.visualizzaMessaggio(INDIRIZZO_NON_VALIDO);
            return false;
        }

        String[] parti = indirizzo.split(VIRGOLA_SEPARATORE);

        if (parti.length == 0 || parti.length == 1 || parti.length == 2 || parti.length > LUNGHEZZA_MINIMA_INDIRIZZO) {
            outputPrinter.visualizzaMessaggio(INDIRIZZO_NON_VALIDO);
            return false;
        } else if (parti.length == LUNGHEZZA_MINIMA_INDIRIZZO) {
            try {
                Integer.parseInt(parti[INDICE_NUMERO_CIVICO].trim());
            } catch (NumberFormatException e) {
                outputPrinter.visualizzaMessaggio(ERRORE_NUMERO_CIVICO_NON_VALIDO);
                return false;
            }
        } else if (parti[0].trim() != ambito) {
            outputPrinter.visualizzaMessaggio(HAI_INSERITO_AMBITO_ERRATO);
        }

        String comune = parti[0].trim();
        return ambito.toLowerCase().equals(comune.toLowerCase());
    }

    public void aggiungiTipoVisita(CorpoDati dati) {
        String nome = InputReader.leggiStringaNonVuota(INSERISCI_NOME_LUOGO);
        ArrayList<Luogo> luoghi = dati.getLuoghi();
        boolean trovato = false;
        setGestioneTipoVisita(new GestioneTipoVisita(repositoryManager));
        for (Luogo luogo : luoghi) {
            if (luogo.getNomeIdentificativo().equalsIgnoreCase(nome)) {
                TipoVisita tipoVisita = gestioneTipoVisita.creaTipoVisita(dati.getOrganizzazione());
                luogo.getTipiVisita().add(tipoVisita);
                trovato = true;
                repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(dati);
            }
        }
        if (!trovato) {
            outputPrinter.visualizzaMessaggio(NESSUN_LUOGO_CON_NOME + nome);
        }

    }

}
