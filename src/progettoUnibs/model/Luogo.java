package progettoUnibs.model;

import java.util.ArrayList;


public class Luogo {

    private String nomeIdentificativo;
    private String descrizione;
    private String indirizzo;
    private ArrayList<TipoVisita> tipiVisita;

    public Luogo(String nomeIdentificativo, String descrizione, String indirizzo, ArrayList<TipoVisita> tipoVisita) {
        this.nomeIdentificativo = nomeIdentificativo;
        this.descrizione = descrizione;
        this.indirizzo = indirizzo;
        this.tipiVisita = tipoVisita;
        for (TipoVisita t : tipiVisita) {
            t.setLuogo(this);
        }
    }

    public String getNomeIdentificativo() {
        return nomeIdentificativo;
    }

    public void setNomeIdentificativo(String nomeIdentificativo) {
        this.nomeIdentificativo = nomeIdentificativo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public ArrayList<TipoVisita> getTipiVisita() {
        return tipiVisita;
    }

    public void setTipiVisita(ArrayList<TipoVisita> tipiVisita) {
        this.tipiVisita = tipiVisita;
    }

    public boolean rimuoviTipoVisitaSenzaVolontari(CorpoDati dati, TipoVisita tipoVisita) { 
        if (tipoVisita.getVolontari().isEmpty()) {
            getTipiVisita().remove(tipoVisita);
            return true;
        }
        return false;
    }

                        
}