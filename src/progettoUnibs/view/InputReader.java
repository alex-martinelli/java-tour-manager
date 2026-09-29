package progettoUnibs.view;

import java.time.*;
import java.time.format.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class InputReader {

    private static final String INSERISCI_GIORNO_SETTIMANA = "Inserisci un giorno della settimana (es. MONDAY): ";
    private static final String ERRORE_GIORNO_NON_VALIDO = "Errore: Giorno non valido. Inserisci un valore corretto (es. MONDAY, TUESDAY...).";
    private static final String VUOI_AGGIUNGERE_ALTRO_GIORNO = "Vuoi aggiungere un altro giorno?";
    private static final String FORMATO_ERRATO = "Formato errato! Inserire la data nel formato --MM-DD.";
    private static final String ORARIO_INIZIO = "Orario inizio (HH:MM)";
    private static final String FORMATO_ORARIO_NON_VALIDO = "Formato orario non valido! Usa il formato HH:MM (es. 14:30).";
    private final static String ERRORE_FORMATO = "Attenzione: il dato inserito non e' nel formato corretto";
    private final static String ERRORE_MINIMO = "Attenzione: e' richiesto un valore maggiore o uguale a ";
    private final static String ERRORE_STRINGA_VUOTA = "Attenzione: non hai inserito alcun carattere";
    private final static String ERRORE_MASSIMO = "Attenzione: e' richiesto un valore minore o uguale a ";
    private final static String MESSAGGIO_AMMISSIBILI = "Attenzione: i caratteri ammissibili sono: ";
    private final static char RISPOSTA_SI = 'S';
    private final static char RISPOSTA_NO = 'N';
    private static final String PATTERN_ORARIO = "HH:mm";
    private static final String STRINGA_VUOTA = "";
    private static final int VALORE_INIZIALE_INTERO = 0;
    private static final char VALORE_INIZIALE_CHAR = '\0';
    private static final int INDICE_NON_TROVATO = -1;
    private static Scanner lettore = creaScanner();

    public void leggiDayOfWeek(List<DayOfWeek> giorniDisponibili) {
        boolean continua;

        do {
            String giorno = InputReader.leggiStringaNonVuota(INSERISCI_GIORNO_SETTIMANA).toUpperCase();

            try {
                giorniDisponibili.add(DayOfWeek.valueOf(giorno));
                continua = InputReader.yesOrNo(VUOI_AGGIUNGERE_ALTRO_GIORNO);
            } catch (IllegalArgumentException e) {
                System.out.println(ERRORE_GIORNO_NON_VALIDO);
                continua = true;
            }
        } while (continua);
    }

    public MonthDay leggiMonthDay(String messaggio) {
        while (true) {
            try {
                System.out.println(messaggio);
                String input = InputReader.leggiStringaNonVuota(STRINGA_VUOTA).trim();
                return MonthDay.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println(FORMATO_ERRATO);
            }
        }
    }

    public LocalTime leggiOrario() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_ORARIO);
        LocalTime orario = null;

        while (orario == null) {
            try {
                String input = InputReader.leggiStringaNonVuota(ORARIO_INIZIO);
                orario = LocalTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println(FORMATO_ORARIO_NON_VALIDO);
            }
        }
        return orario;

    }




    private static Scanner creaScanner() {
        Scanner creato = new Scanner(System.in);
        creato.useDelimiter(System.getProperty("line.separator"));
        return creato;
    }

    public static String leggiStringa(String messaggio) {
        System.out.print(messaggio);
        return lettore.next();
    }

    public static String leggiStringaNonVuota(String messaggio) {
        boolean finito = false;
        String lettura = null;
        do {
            lettura = leggiStringa(messaggio);
            lettura = lettura.trim();
            if (lettura.length() > 0)
                finito = true;
            else
                System.out.println(ERRORE_STRINGA_VUOTA);
        } while (!finito);

        return lettura;
    }

    public static int leggiIntero(String messaggio) {
        boolean finito = false;
        int valoreLetto = VALORE_INIZIALE_INTERO;
        do {
            System.out.print(messaggio);
            try {
                valoreLetto = lettore.nextInt();
                finito = true;
            } catch (InputMismatchException e) {
                System.out.println(ERRORE_FORMATO);
                @SuppressWarnings("unused")
                String daButtare = lettore.next();
            }
        } while (!finito);
        return valoreLetto;
    }

    public static int leggiInteroConMinimo(String messaggio, int minimo) {
        boolean finito = false;
        int valoreLetto = VALORE_INIZIALE_INTERO;
        do {
            valoreLetto = leggiIntero(messaggio);
            if (valoreLetto >= minimo)
                finito = true;
            else
                System.out.println(ERRORE_MINIMO + minimo);
        } while (!finito);

        return valoreLetto;
    }

    public static int leggiIntero(String messaggio, int minimo, int massimo) {
        boolean finito = false;
        int valoreLetto = VALORE_INIZIALE_INTERO;
        do {
            valoreLetto = leggiIntero(messaggio);
            if (valoreLetto >= minimo && valoreLetto <= massimo)
                finito = true;
            else if (valoreLetto < minimo)
                System.out.println(ERRORE_MINIMO + minimo);
            else
                System.out.println(ERRORE_MASSIMO + massimo);
        } while (!finito);

        return valoreLetto;
    }

    public static char leggiChar(String messaggio) {
        boolean finito = false;
        char valoreLetto = VALORE_INIZIALE_CHAR;
        do {
            System.out.print(messaggio);
            String lettura = lettore.next();
            if (lettura.length() > 0) {
                valoreLetto = lettura.charAt(0);
                finito = true;
            } else {
                System.out.println(ERRORE_STRINGA_VUOTA);
            }
        } while (!finito);
        return valoreLetto;
    }

    public static char leggiUpperChar(String messaggio, String ammissibili) {
        boolean finito = false;
        char valoreLetto = VALORE_INIZIALE_CHAR;
        do {
            valoreLetto = leggiChar(messaggio);
            valoreLetto = Character.toUpperCase(valoreLetto);
            if (ammissibili.indexOf(valoreLetto) != INDICE_NON_TROVATO)
                finito = true;
            else
                System.out.println(MESSAGGIO_AMMISSIBILI + ammissibili);
        } while (!finito);
        return valoreLetto;
    }

    public static boolean yesOrNo(String messaggio) {
        String mioMessaggio = messaggio + "(" + RISPOSTA_SI + "/" + RISPOSTA_NO + ")";
        char valoreLetto = leggiUpperChar(mioMessaggio, String.valueOf(RISPOSTA_SI) + String.valueOf(RISPOSTA_NO));
        if (valoreLetto == RISPOSTA_SI)
            return true;
        else
            return false;
    }

}
