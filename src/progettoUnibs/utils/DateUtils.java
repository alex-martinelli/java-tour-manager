package progettoUnibs.utils;

import java.time.LocalDate;
import java.time.Month;

public class DateUtils {
    
    private static final int MESE_MASSIMO_SENZA_OVERFLOW = 11;
    private static final int INCREMENTO_MESE = 1;
    private static final int PRIMO_MESE_ANNO = 1;
    private static final int MESI_ANNO = 12;
    private static final int OFFSET_MESE_CALCOLO = 2;
    
     public static Month getMeseSuccessivo() {
        LocalDate dataMeseSuccessivo;
        if (LocalDate.now().getMonthValue() <= MESE_MASSIMO_SENZA_OVERFLOW) {
            dataMeseSuccessivo = LocalDate.now().plusMonths(INCREMENTO_MESE);
        } else {
            dataMeseSuccessivo = LocalDate.now().plusYears(INCREMENTO_MESE).withMonth(PRIMO_MESE_ANNO);
        }
        Month meseSuccessivo = dataMeseSuccessivo.getMonth();
        return meseSuccessivo;
    }
    
     public static Month getMeseCorretto(int i) {
        Month mese = Month.of(LocalDate.now().getMonthValue() + i);
        if (mese.getValue() > MESI_ANNO) {
            mese = Month.of(mese.getValue() - MESI_ANNO);
        }
        return mese;
    }

      public static int calcolaMeseCorrente(int currentMonth) {
        int month2Val = currentMonth + OFFSET_MESE_CALCOLO;
        if (month2Val > MESI_ANNO) {
            month2Val -= MESI_ANNO;
        }
        return month2Val;
    }


}
