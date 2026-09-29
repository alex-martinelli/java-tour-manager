package progettoUnibs.model;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class TipoVisita {

    private String titolo;
    private String descrizione;
    private String puntoIncontro;
    private boolean bigliettoNecessario;
    private int minimoPartecipanti;
    private int minutiDurata;
    private int massimoPartecipanti;
    private MonthDay dataInizio;
    private MonthDay dataFine;
    private List<DayOfWeek> giorniDisponibili;
    private LocalTime oraInizio;
    private ArrayList<String> volontari = new ArrayList<>();
    private transient Luogo luogo;

    public TipoVisita(String titolo, String descrizione, String puntoIncontro, boolean bigliettoNecessario,
            int minimoPartecipanti, int minutiDurata, int massimoPartecipanti, MonthDay dataInizio, MonthDay dataFine,
            List<DayOfWeek> giorniDisponibili, LocalTime oraInizio, ArrayList<String> volontari) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.bigliettoNecessario = bigliettoNecessario;
        this.minimoPartecipanti = minimoPartecipanti;
        this.minutiDurata = minutiDurata;
        this.massimoPartecipanti = massimoPartecipanti;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.giorniDisponibili = giorniDisponibili;
        this.oraInizio = oraInizio;
        this.volontari = volontari;
    }

    public Luogo getLuogo() {
        return luogo;
    }

    public void setLuogo(Luogo luogo) {
        this.luogo = luogo;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPuntoIncontro() {
        return puntoIncontro;
    }

    public void setPuntoIncontro(String puntoIncontro) {
        this.puntoIncontro = puntoIncontro;
    }

    public Boolean getBigliettoNecessario() {
        return bigliettoNecessario;
    }

    public void setBigliettoNecessario(Boolean bigliettoNecessario) {
        this.bigliettoNecessario = bigliettoNecessario;
    }

    public int getMinimoPartecipanti() {
        return minimoPartecipanti;
    }

    public void setMinimoPartecipanti(int minimoPartecipanti) {
        this.minimoPartecipanti = minimoPartecipanti;
    }

    public int getMinutiDurata() {
        return minutiDurata;
    }

    public void setMinutiDurata(int minutiDurata) {
        this.minutiDurata = minutiDurata;
    }

    public int getMassimoPartecipanti() {
        return massimoPartecipanti;
    }

    public void setMassimoPartecipanti(int massimoPartecipanti) {
        this.massimoPartecipanti = massimoPartecipanti;
    }

    public MonthDay getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(MonthDay dataInizio) {
        this.dataInizio = dataInizio;
    }

    public MonthDay getDataFine() {
        return dataFine;
    }

    public void setDataFine(MonthDay dataFine) {
        this.dataFine = dataFine;
    }

    public List<DayOfWeek> getGiorniDisponibili() {
        return giorniDisponibili;
    }

    public void setGiorniDisponibili(List<DayOfWeek> giorniDisponibili) {
        this.giorniDisponibili = giorniDisponibili;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public ArrayList<String> getVolontari() {
        return volontari;
    }

    public void setVolontari(ArrayList<String> volontari) {
        this.volontari = volontari;
    }

    public static boolean checkOrario(TipoVisita t1, TipoVisita t2) {
        LocalTime inizio1 = t1.getOraInizio();
        LocalTime fine1 = inizio1.plusMinutes(t1.getMinutiDurata());
        LocalTime inizio2 = t2.getOraInizio();
        LocalTime fine2 = inizio2.plusMinutes(t2.getMinutiDurata());

        return inizio1.isBefore(fine2) && inizio2.isBefore(fine1);
    }

    public boolean rimuoviVolontario(String nickname) {
        if(getVolontari().contains(nickname)){
            getVolontari().remove(nickname);
            setVolontari(getVolontari());
            return true;
        }
        return false;
    }

    public boolean volontarioOccupatoPerTipoVisita(CorpoDati dati, String nickname, String organizzazione) {
        for (Luogo luogo : dati.getLuoghi()) {
            for (TipoVisita t : luogo.getTipiVisita()) {

                if (!(getDataFine().isBefore(t.getDataInizio())
                        || t.getDataFine().isBefore(getDataInizio()))) {

                    List<DayOfWeek> intersection = t.getGiorniDisponibili().stream()
                            .filter(d -> getGiorniDisponibili().contains(d))
                            .collect(Collectors.toList());

                    if (!intersection.isEmpty()) {

                        if (t.getVolontari().contains(nickname)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}