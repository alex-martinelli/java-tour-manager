package progettoUnibs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CorpoDati {

    private static final String JSON_KEY_NOME_IDENTIFICATIVO = "nomeIdentificativo";
    private static final String JSON_KEY_TITOLO = "titolo";
    private static final int GIORNO_LIMITE_MESE = 16;

    private ArrayList<Luogo> luoghi = new ArrayList<>();
    private String organizzazione;
    private String ambitoTerritoriale;
    private int maxPersonePerIscrizione;
    private LocalDate dataAvvioAttivita;

    public CorpoDati(ArrayList<Luogo> luoghi, String organizzazione, String ambito, int persone) {
        this.luoghi = luoghi;
        this.organizzazione = organizzazione;
        this.ambitoTerritoriale = ambito;
        this.maxPersonePerIscrizione = persone;
    }

    public ArrayList<Luogo> getLuoghi() {
        return luoghi;
    }

    public void setLuoghi(ArrayList<Luogo> luoghi) {
        this.luoghi = luoghi;
    }

    public String getOrganizzazione() {
        return organizzazione;
    }

    public void setOrganizzazione(String organizzazione) {
        this.organizzazione = organizzazione;
    }

    public String getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public void setAmbitoTerritoriale(String ambitoTerritoriale) {
        this.ambitoTerritoriale = ambitoTerritoriale;
    }

    public LocalDate getDataAvvioAttivita() {
        return dataAvvioAttivita;
    }

    public void setDataAvvioAttivita(LocalDate dataAvvioAttivita) {
        this.dataAvvioAttivita = dataAvvioAttivita;
    }

    public int getMaxPersonePerIscrizione() {
        return maxPersonePerIscrizione;
    }

    public void setMaxPersonePerIscrizione(int maxPersonePerIscrizione) {
        this.maxPersonePerIscrizione = maxPersonePerIscrizione;
    }

    public void aggiuntaTipoVisitaaLuogo(ArrayList<Luogo> luoghi, Luogo l, TipoVisita tipoVisita) {
        for (Luogo luogo : luoghi) {
            if (luogo.getNomeIdentificativo().equals(l.getNomeIdentificativo())) {
                luogo.getTipiVisita().add(tipoVisita);
                return;
            }
        }
        l.getTipiVisita().add(tipoVisita);
        luoghi.add(l);
    }

    public void impostaDataAvvioAttivita() {
        if (LocalDate.now().getDayOfMonth() <= GIORNO_LIMITE_MESE) {
            setDataAvvioAttivita(LocalDate.now());
        } else {
            setDataAvvioAttivita(LocalDate.now().plusMonths(1));
        }
    }

    public boolean esisteLuogo(String nomeIdentificativo) {
        for (Luogo luogo : getLuoghi()) {
            if (luogo.getNomeIdentificativo().equals(nomeIdentificativo)) {
                return true;
            }
        }
        return false;
    }

    public boolean rimuoviLuogoSenzaTipoVisita(Luogo luogo) {
        if (luogo.getTipiVisita().isEmpty()) {
            getLuoghi().remove(luogo);
            return true;
        }

        return false;
    }

    public boolean rimuoviLuogoDaCorpoDati(String nome) {
        Iterator<Luogo> iterLuoghi = luoghi.iterator();
        while (iterLuoghi.hasNext()) {
            Luogo luogo = iterLuoghi.next();
            if (luogo.getNomeIdentificativo().equalsIgnoreCase(nome)) {
                iterLuoghi.remove();
                return true;
            }
        }
        return false;
    }

    public boolean rimuoviLuogoSenzaTipiVisita(Luogo luogo) {
        if (luogo.getTipiVisita().isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean rimuoviTipoVisitaCorpoDati(String nome) {
        Iterator<Luogo> iterLuoghi = luoghi.iterator();
        while (iterLuoghi.hasNext()) {
            Luogo luogo = iterLuoghi.next();
            Iterator<TipoVisita> iterTipiVisita = luogo.getTipiVisita().iterator();
            while (iterTipiVisita.hasNext()) {
                TipoVisita tipoVisita = iterTipiVisita.next();
                if (tipoVisita.getTitolo().equalsIgnoreCase(nome)) {
                    iterTipiVisita.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<String> getNomiLuoghi(JsonArray luoghi) {
        ArrayList<String> nomiLuoghi = new ArrayList<>();
        for (JsonElement elemento : luoghi) {
            JsonObject luogo = elemento.getAsJsonObject();
            String nome = luogo.get(JSON_KEY_NOME_IDENTIFICATIVO).getAsString();
            nomiLuoghi.add(nome);
        }
        return nomiLuoghi;
    }

    public static ArrayList<String> getNomiTipiVisita(JsonArray tipiVisita) {
        ArrayList<String> nomiTipiVisita = new ArrayList<>();
        for (JsonElement elemento : tipiVisita) {
            JsonObject visita = elemento.getAsJsonObject();
            String titolo = visita.get(JSON_KEY_TITOLO).getAsString();
            nomiTipiVisita.add(titolo);
        }
        return nomiTipiVisita;
    }

}
