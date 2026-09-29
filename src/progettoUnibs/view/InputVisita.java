
package progettoUnibs.view;

import java.time.MonthDay;


public class InputVisita {
    
    private static final String MESSAGGIO_NOME_VISITA = "\n Inserisci il nome della visita della quale vuoi visualizzare le prenotazioni: ";
    private static final String MESSAGGIO_DATA_VISITA = "Inserisci la data della visita (MM-DD): ";
    
    public static InputReader inputReader= new InputReader();

    public String askNomeVisita() {
        String nomeVisita = InputReader
                .leggiStringaNonVuota(MESSAGGIO_NOME_VISITA);
        return nomeVisita;
    }

    public MonthDay askDataVisita() {
        MonthDay dataVisita = inputReader.leggiMonthDay(MESSAGGIO_DATA_VISITA);
        return dataVisita;
    }
}