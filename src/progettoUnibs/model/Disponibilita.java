package progettoUnibs.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.*;


public class Disponibilita {

    private static final int GIORNO_LIMITE_MESE = 15;
    private static final int OFFSET_MESE_PRIMA_META = 1;
    private static final int OFFSET_MESE_SECONDA_META = 2;
    private static final int MESI_ANNO = 12;

    private boolean raccoltaAperta;
    private Month mese;
    private Map<String, List<MonthDay>> disponibilitaMap = new HashMap<>();

    public Disponibilita(boolean raccoltaAperta, Month mese, Map<String, List<MonthDay>> disponibilitaMap) {
        this.raccoltaAperta = raccoltaAperta;
        this.mese = mese;
        this.disponibilitaMap = disponibilitaMap;
    }

    public boolean isRaccoltaAperta() {
        return raccoltaAperta;
    }

    public void setRaccoltaAperta(boolean raccoltaAperta) {
        this.raccoltaAperta = raccoltaAperta;
    }

    public Month getMese() {
        return mese;
    }

    public void setMese(Month mese) {
        this.mese = mese;
    }

    public Map<String, List<MonthDay>> getDisponibilitaMap() {
        return disponibilitaMap;
    }

    public void setDisponibilitaMap(Map<String, List<MonthDay>> disponibilitaMap) {
        this.disponibilitaMap = disponibilitaMap;
    }

    public static Month getMeseDisponibilita() {
        LocalDate now = LocalDate.now();
        int offset = now.getDayOfMonth() <= GIORNO_LIMITE_MESE ? OFFSET_MESE_PRIMA_META : OFFSET_MESE_SECONDA_META;
        int targetMonth = now.getMonthValue() + offset;
        if (targetMonth > MESI_ANNO) {
            targetMonth = targetMonth - MESI_ANNO;
        }
        Month meseDisponibilita = Month.of(targetMonth);

        return meseDisponibilita;
    }

  
    public static Disponibilita getDisponibilitaCorrente(boolean dimNulla, Month meseOffset1, Disponibilita dispOffset1,
            Month meseOffset2, Disponibilita dispOffset2) {
        // Se il mese corrente ha raccolta aperta, lo restituiamo

        // Se il mese successivo ha raccolta aperta, scegliamo quello
        if (dimNulla && dispOffset1 != null && dispOffset1.isRaccoltaAperta()) {
            dispOffset1.setMese(meseOffset1);
            return dispOffset1;
        }
        // Altrimenti proviamo con mese+2
        else if (dispOffset2 != null && dispOffset2.isRaccoltaAperta()) {
            dispOffset2.setMese(meseOffset2);
            return dispOffset2;
        }
        return null;
    }

}
