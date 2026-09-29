package progettoUnibs.controller;

import java.io.FileReader;
import java.io.IOException;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Luogo;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.ViewCorpoDati;

public class GestioneCorpoDati {

    private static final String INSERISCI_NUMERO_MASSIMO_PERSONE = "Inserisci nuovo numero per massimo persone: ";
    private static final String INSERISCI_AMBITO_TERRITORIALE = "Inserisci ambito territoriale (Comune/i)\n";
    private static final String FISSARE_NUMERO_MASSIMO_PERSONE = "Fissare il numero massimo di persone che un fruitore può iscrivere a un'iniziativa\nmediante una singola iscrizione: ";
    private static final String DEVI_AGGIUNGERE_ALMENO_UN_LUOGO = "Devi aggiungere almeno un luogo prima di proseguire!";
    private static final String TUTTI_VOLONTARI_ASSEGNATI = "Tutti i volontari devono essere assegnati ad almeno un tipo di visita";
    private static final String ERRORE_LETTURA_FILE = "Errore nella lettura del file: ";
    private static final String NOMI_IDENTIFICATIVI_LUOGHI = "Nomi identificativi dei luoghi:";
    private static final String TIPI_VISITA_LUOGO = "\nTipi di visita per ogni luogo:\n";
    private static final String STRUTTURA_FILE_NON_VALIDA = "Struttura del file non valida.";
    private static final String NESSUN_LUOGO_TROVATO = "Nessun luogo trovato nel file.";
    private static final String NESSUNA_VISITA_TROVATA = "Nessuna visita trovata nel luogo";
    private static final String JSON_EXTENSION = ".json";
    private static final String CREAZIONE_CORPO_DATI = "Creazione corpo dati\n";
    private static final String AGGIUNTA_LUOGO = "Aggiunta luogo:";
    private static final String INSERISCI_NICKNAME_RIMUOVI = "Inserisci nickname del volontario da rimuovere: ";
    private static final String NICKNAME_NON_PRESENTE = "Il nickname inserito non è presente nella lista dei volontari";
    private static final String VOLONTARIO_UNICO_TIPO_VISITA = "Il volontario rimosso era l'unico assegnato al tipo visita ";
    private static final String TIPO_VISITA_RIMOSSO = ", verrà rimosso anche il tipo visita";
    private static final String TIPO_VISITA_UNICO_LUOGO = "Il tipo di visita rimosso era l'unico assegnato al luogo ";
    private static final String LUOGO_RIMOSSO = ", verrà rimosso anche il luogo";
    private static final String INSERISCI_NOME_LUOGO_RIMUOVI = "Inserisci nome luogo da rimuovere: ";
    private static final String NESSUN_LUOGO_CON_NOME = "Nessun luogo trovato con il nome: ";
    private static final String VOLONTARI_RIMOSSI = "I seguenti volontari non hanno più assegnazioni e verranno rimossi: ";
    private static final String INSERISCI_NOME_TIPO_VISITA_RIMUOVI = "Inserisci nome tipo visita da rimuovere: ";
    private static final String NESSUN_TIPO_VISITA_CON_NOME = "Nessun tipo di visita trovato con il nome: ";
    private static final String CORPO_DATI_KEY = "corpoDati";
    private static final String LUOGHI_KEY = "luoghi";
    private static final String NOME_IDENTIFICATIVO_KEY = "nomeIdentificativo";
    private static final String TIPI_VISITA_KEY = "tipiVisita";
    private static final String LUOGO_PREFIX = "Luogo: ";

    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneMenuConfiguratore gestioneMenuConfiguratore;
    private RepositoryManager repositoryManager;
    private GestioneLuogo gestioneLuogo;
    private GestioneVolontari gestioneVolontari;
    private GestioneMenuCorpoDati gestioneMenuCorpoDati;
    private CorpoDati dati;
    private String filePath;

    public GestioneCorpoDati(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneLuogo(new GestioneLuogo(repositoryManager));
        setGestioneMenuCorpoDati(new GestioneMenuCorpoDati(repositoryManager));
    }

    public CorpoDati getDati() {
        return dati;
    }

    public void setDati(CorpoDati dati) {
        this.dati = dati;
        dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(dati.getOrganizzazione());
        filePath = FileUtils.buildFilePath(dati.getOrganizzazione(), dati.getOrganizzazione() + JSON_EXTENSION);
    }

    public void setGestioneMenuConfiguratore(GestioneMenuConfiguratore gestioneMenuConf) {
        gestioneMenuConfiguratore = gestioneMenuConf;
    }

    public void setGestioneLuogo(GestioneLuogo gestioneL) {
        gestioneLuogo = gestioneL;
    }

    public void setGestioneVolontari(GestioneVolontari gestioneVol) {
        gestioneVolontari = gestioneVol;
    }

    public void setGestioneMenuCorpoDati(GestioneMenuCorpoDati gestioneMenuCorpoDati) {
        this.gestioneMenuCorpoDati = gestioneMenuCorpoDati;
    }

    public void creaCorpoDati(String organizzazione) {
        outputPrinter.visualizzaMessaggio(CREAZIONE_CORPO_DATI);

        String ambito = InputReader.leggiStringaNonVuota(INSERISCI_AMBITO_TERRITORIALE);
        int MaxPersone = InputReader.leggiInteroConMinimo(FISSARE_NUMERO_MASSIMO_PERSONE, 1);

        CorpoDati corpo = new CorpoDati(null, organizzazione, ambito, MaxPersone);
        ArrayList<Luogo> luoghi = new ArrayList<>();
        boolean continua = true;

        while (continua) {
            gestioneMenuCorpoDati.menuCreazioneDati(corpo, continua, luoghi, organizzazione);
        }
    }

    public boolean proseguiCorpoDati(String organizzazione, CorpoDati corpo, ArrayList<Luogo> luoghi,
            boolean continua) {
        setGestioneVolontari(new GestioneVolontari(repositoryManager));
        setGestioneMenuConfiguratore(new GestioneMenuConfiguratore(repositoryManager));
        if (luoghi.isEmpty()) {
            outputPrinter.visualizzaMessaggio(DEVI_AGGIUNGERE_ALMENO_UN_LUOGO);
        } else if (!gestioneVolontari.volontariAssegnati(organizzazione)) {
            outputPrinter.visualizzaMessaggio(TUTTI_VOLONTARI_ASSEGNATI);
        } else {
            gestioneMenuConfiguratore.menuConfiguratore(corpo);
            continua = false;
        }
        return continua;
    }

    public void modificaNumeroMassimoPersone() {
        int massimoPersone = InputReader.leggiInteroConMinimo(INSERISCI_NUMERO_MASSIMO_PERSONE, 1);
        dati.setMaxPersonePerIscrizione(massimoPersone);
        repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(dati);
    }

    public void aggiungiLuogo(CorpoDati dati) {
        outputPrinter.visualizzaMessaggio(AGGIUNTA_LUOGO);
        gestioneLuogo.creazioneLuogo(dati, dati.getLuoghi());
    }

    public void rimuoviVolontario(CorpoDati dati) {
        String nickname = InputReader.leggiStringaNonVuota(INSERISCI_NICKNAME_RIMUOVI);
        Set<String> volontari = repositoryManager.getVolontariRepository()
                .leggiVolontariDaTxt(dati.getOrganizzazione());
        if (!volontari.contains(nickname)) {
            outputPrinter.visualizzaMessaggio(NICKNAME_NON_PRESENTE);
            return;
        }

        repositoryManager.getVolontariRepository().rimuoviVolontarioDaFile(nickname, dati.getOrganizzazione());

        for (Luogo luogo : dati.getLuoghi()) {
            for (TipoVisita tipoVisita : luogo.getTipiVisita()) {
                if (tipoVisita.rimuoviVolontario(nickname)) {
                    if (luogo.rimuoviTipoVisitaSenzaVolontari(dati, tipoVisita)) {
                        outputPrinter.visualizzaMessaggio(VOLONTARIO_UNICO_TIPO_VISITA
                                + tipoVisita.getTitolo() + TIPO_VISITA_RIMOSSO);
                        if (dati.rimuoviLuogoSenzaTipoVisita(luogo)) {
                            outputPrinter
                                    .visualizzaMessaggio(TIPO_VISITA_UNICO_LUOGO
                                            + luogo.getNomeIdentificativo() + LUOGO_RIMOSSO);
                        }

                        repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(dati);
                        return;
                    }
                }
            }
        }

    }

    public void rimuoviLuogo(CorpoDati dati) {
        String nome = InputReader.leggiStringaNonVuota(INSERISCI_NOME_LUOGO_RIMUOVI);
        ArrayList<Luogo> luoghi = dati.getLuoghi();
        boolean trovato = false;
        trovato = dati.rimuoviLuogoDaCorpoDati(nome);
        if (!trovato) {
            outputPrinter.visualizzaMessaggio(NESSUN_LUOGO_CON_NOME + nome);
            return;
        }

        dati.setLuoghi(luoghi);
        repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(dati);

        rimozioneVolontariNonOccupati(dati);
    }

    private void rimozioneVolontariNonOccupati(CorpoDati dati) {
        Set<String> volontariAttivi = new HashSet<>();
        for (Luogo luogo : dati.getLuoghi()) {
            for (TipoVisita tipoVisita : luogo.getTipiVisita()) {
                volontariAttivi.addAll(tipoVisita.getVolontari());
            }
        }
        Set<String> volontariDaRimuovere = repositoryManager.getVolontariRepository()
                .leggiVolontariDaTxt(dati.getOrganizzazione());
        volontariDaRimuovere.removeAll(volontariAttivi);

        if (!volontariDaRimuovere.isEmpty()) {
            outputPrinter.visualizzaMessaggio(
                    VOLONTARI_RIMOSSI + volontariDaRimuovere);
            for (String nickname : volontariDaRimuovere) {
                repositoryManager.getVolontariRepository().rimuoviVolontarioDaFile(nickname, dati.getOrganizzazione());
            }
        }
    }

    public void rimuoviTipoVisita(CorpoDati dati) {
        String nome = InputReader.leggiStringaNonVuota(INSERISCI_NOME_TIPO_VISITA_RIMUOVI);
        ArrayList<Luogo> luoghi = dati.getLuoghi();
        boolean trovato = dati.rimuoviTipoVisitaCorpoDati(nome);
        if (trovato) {
            Iterator<Luogo> iterLuoghi = luoghi.iterator();
            while (iterLuoghi.hasNext()) {
                Luogo luogo = iterLuoghi.next();
                if (dati.rimuoviLuogoSenzaTipiVisita(luogo)) {
                    outputPrinter.visualizzaMessaggio(TIPO_VISITA_UNICO_LUOGO
                            + luogo.getNomeIdentificativo() + LUOGO_RIMOSSO);
                    iterLuoghi.remove();
                }
            }
        } else {
            outputPrinter.visualizzaMessaggio(NESSUN_TIPO_VISITA_CON_NOME + nome);
            return;
        }
        dati.setLuoghi(luoghi);
        repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(dati);
        rimozioneVolontariNonOccupati(dati);

    }

    public void esisteCorpoDati(String organizzazione) {
        if (!repositoryManager.getCorpoDatiRepository().isDataCreated(organizzazione)) {
            if (ViewCorpoDati.chiediSeVuoiCreareCorpoDati()) {
                creaCorpoDati(organizzazione);
            }
        } else {
            ViewCorpoDati.mostraCorpoDatiEsistente();
        }
    }

    public void visualizzaLuoghi() {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject corpoDati = jsonObject.getAsJsonObject(CORPO_DATI_KEY);
            if (corpoDati != null) {
                JsonArray luoghi = corpoDati.getAsJsonArray(LUOGHI_KEY);
                if (luoghi != null) {
                    outputPrinter.visualizzaMessaggio(NOMI_IDENTIFICATIVI_LUOGHI);
                    ArrayList<String> nomiLuoghi = CorpoDati.getNomiLuoghi(luoghi);
                    ViewCorpoDati.visualizzaNomiLuoghi(nomiLuoghi);
                } else {
                    outputPrinter.visualizzaMessaggio(NESSUN_LUOGO_TROVATO);
                }
            } else {
                outputPrinter.visualizzaMessaggio(STRUTTURA_FILE_NON_VALIDA);
            }
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
        }
    }

    public void visualizzaTipiVisite() {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject corpoDati = jsonObject.getAsJsonObject(CORPO_DATI_KEY);
            if (corpoDati != null) {
                JsonArray luoghi = corpoDati.getAsJsonArray(LUOGHI_KEY);
                if (luoghi != null) {
                    outputPrinter.visualizzaMessaggio(TIPI_VISITA_LUOGO);
                    for (JsonElement elemento : luoghi) {
                        JsonObject luogo = elemento.getAsJsonObject();
                        outputPrinter.visualizzaMessaggio(LUOGO_PREFIX + luogo.get(NOME_IDENTIFICATIVO_KEY));
                        JsonArray tipiVisita = luogo.getAsJsonArray(TIPI_VISITA_KEY);
                        ArrayList<String> nomiTipiVisita = CorpoDati.getNomiTipiVisita(tipiVisita);
                        if (nomiTipiVisita != null) {
                            ViewCorpoDati.visualizzaNomiTipiVisita(nomiTipiVisita);
                        } else {
                            outputPrinter.visualizzaMessaggio(NESSUNA_VISITA_TROVATA);
                        }
                    }
                } else {
                    outputPrinter.visualizzaMessaggio(NESSUN_LUOGO_TROVATO);
                }
            } else {
                outputPrinter.visualizzaMessaggio(STRUTTURA_FILE_NON_VALIDA);
            }
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
        }
    }

}