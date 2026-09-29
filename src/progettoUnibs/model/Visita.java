package progettoUnibs.model;

import java.time.MonthDay;
import java.util.*;

public class Visita {

    private static final int NUMERO_ISCRITTI_INIZIALE = 0;

    private TipoVisita tipo;
    private StatoVisita stato;
    private MonthDay data;
    private String volontario;
    private int numeroIscritti = NUMERO_ISCRITTI_INIZIALE;
    private ArrayList<Prenotazione> prenotazioni = new ArrayList<>();

    public Visita(TipoVisita tipo, MonthDay data, String volontario, int numeroIscritti,
            ArrayList<Prenotazione> prenotazioni) {
        this.tipo = tipo;
        this.data = data;
        this.volontario = volontario;
        this.numeroIscritti = numeroIscritti;
        this.prenotazioni = prenotazioni;
        this.stato = new StatoProposta();
    }

    public int getNumeroIscritti() {
        return numeroIscritti;
    }

    public void setNumeroIscritti(int numeroIscritti) {
        this.numeroIscritti = numeroIscritti;
    }

    public ArrayList<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(ArrayList<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    public String getVolontario() {
        return volontario;
    }

    public void setVolontario(String volontario) {
        this.volontario = volontario;
    }

    public MonthDay getData() {
        return data;
    }

    public void setData(MonthDay data) {
        this.data = data;
    }

    public TipoVisita getTipoVisita() {
        return tipo;
    }

    public void setTipoVisita(TipoVisita tipo) {
        this.tipo = tipo;
    }

    public StatoVisita getStato() {
        return stato;
    }

    public void setStato(StatoVisita stato) {
        this.stato = stato;
    }

    public boolean isPrecluso(ArrayList<MonthDay> giorniPreclusi) {
        for (MonthDay giorno : giorniPreclusi) {
            if (data.equals(giorno)) {
                return true;
            }
        }
        return false;
    }

    public void rimozionePrenotazione(int codicePrenotazione, String fruitore) {
        ArrayList<Prenotazione> prenotazioni = getPrenotazioni();
        Iterator<Prenotazione> iterator = prenotazioni.iterator();
        while (iterator.hasNext()) {
            Prenotazione p = iterator.next();
            if (p.getCodice() == codicePrenotazione && p.getNomeFruitore().equals(fruitore)) {
                iterator.remove();
                setPrenotazioni(prenotazioni);
                setNumeroIscritti(getNumeroIscritti() - p.getNumeroPersone());
                break;
            }
        }
    }

    public void aggiornaStatoVisita(Attivita a) {
        ArrayList<Visita> visite = a.getVisite();
        for (int i = 0; i < visite.size(); i++) {
            Visita visita = visite.get(i);
            if (visita.getData().equals(getData())) {
                visita.aggiornaStato(this);
                break;
            }
        }
    }

    private void aggiornaStato(Visita visita) {
        stato.aggiornaStatoVis(this, visita);
    }

    public int calcolaMaxIscrizioni(int maxPersonePerIscrizione) {
        return (getTipoVisita().getMassimoPartecipanti()
                - getNumeroIscritti()) > maxPersonePerIscrizione ? maxPersonePerIscrizione
                        : (getTipoVisita().getMassimoPartecipanti() - getNumeroIscritti());
    }

}
