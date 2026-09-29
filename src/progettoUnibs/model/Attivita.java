package progettoUnibs.model;

import java.time.*;
import java.util.*;

public class Attivita {

    private static final String IL_GIORNO_NON_APPARTIENE_AL_MESE = "Il giorno non appartiene al mese";
    private static final int INCREMENTO_GIORNO = 1;
    private Month mese;
    private ArrayList<MonthDay> giorniPreclusi = new ArrayList<>();
    private ArrayList<Visita> visite = new ArrayList<>();

    public Attivita(Month mese, ArrayList<MonthDay> giorni, ArrayList<Visita> visite) {
        this.mese = mese;
        this.giorniPreclusi = new ArrayList<>();
        for (MonthDay giorno : giorni) {
            if (giorno.getMonth() != mese) {
                throw new IllegalArgumentException(IL_GIORNO_NON_APPARTIENE_AL_MESE);
            }
            this.giorniPreclusi.add(giorno);
        }
        this.visite = visite;
    }

    public Month getMese() {
        return mese;
    }

    public void setMese(Month mese) {
        this.mese = mese;
    }

    public ArrayList<MonthDay> getGiorniPreclusi() {
        return giorniPreclusi;
    }

    public void setGiorni(ArrayList<MonthDay> giorni) {
        this.giorniPreclusi = giorni;
    }

    public ArrayList<Visita> getVisite() {
        return visite;
    }

    public void setVisite(ArrayList<Visita> visite) {
        this.visite = visite;
    }

    public boolean esisteVisitaSovrapposta(TipoVisita nuovaVisita, LocalDate dataVisita) {
        for (Visita visita : visite) {
            LocalDate dataEsistente = visita.getData().atYear(dataVisita.getYear());
            boolean stessoLuogo = visita.getTipoVisita().getLuogo().equals(nuovaVisita.getLuogo());
            TipoVisita tipo = visita.getTipoVisita();
            boolean orarioSovrapposto = TipoVisita.checkOrario(tipo, nuovaVisita);
            boolean stessaData = dataEsistente.equals(dataVisita);

            if (stessoLuogo && orarioSovrapposto && stessaData) {
                return true;
            }
        }
        return false;
    }

    public void modificaPrenotazioniVisita(Visita visita, int numeroIscrizioni,
            Prenotazione prenotazione, ArrayList<Visita> visite) {
        for (Visita v : visite) {
            if (v.getData().equals(visita.getData())
                    && v.getTipoVisita().getTitolo().equals(visita.getTipoVisita().getTitolo())) {
                v.setNumeroIscritti(v.getNumeroIscritti() + numeroIscrizioni);
                ArrayList<Prenotazione> prenotazioni = v.getPrenotazioni();
                prenotazioni.add(prenotazione);
                v.setPrenotazioni(prenotazioni);
                v.getStato().aggiornaStatoNuovaPrenotazione(v);

                break;
            }
        }
        setVisite(visite);
    }


    public  ArrayList<MonthDay> getGiorni(TipoVisita tipo, Month mese) {
        ArrayList<MonthDay> giorni = new ArrayList<>();
        int currentYear = Year.now().getValue();
        LocalDate start = tipo.getDataInizio().atYear(currentYear);
        LocalDate end = tipo.getDataFine().atYear(currentYear);
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(INCREMENTO_GIORNO)) {
            if (date.getMonth() == mese && tipo.getGiorniDisponibili().contains(date.getDayOfWeek())) {
                giorni.add(MonthDay.from(date));
            }
        }

        return giorni;
    }


    public Visita trovaVisitaDaCodicePrenotazioneInAttivita(int codicePrenotazione) {
        ArrayList<Visita> visite=getVisite();
        for (Visita v : visite) {
            for (Prenotazione p : v.getPrenotazioni()) {
                if (p.getCodice() == codicePrenotazione) {
                    return v;
                }
            }
        }
        return null;
    }

}
