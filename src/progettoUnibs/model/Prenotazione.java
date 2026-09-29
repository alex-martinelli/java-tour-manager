package progettoUnibs.model;

import java.util.ArrayList;
import java.util.Random;

public class Prenotazione {
    
    private static final int CODICE_BASE = 1000;
    private static final int CODICE_RANGE = 9000;
    private static final int CODICE_INIZIALE = 0;
    
    private String nomeFruitore;
    private int codice;
    private int numeroPersone;

    public Prenotazione(String nomeFruitore, int codice, int numeroPersone) {
        this.nomeFruitore = nomeFruitore;
        this.codice = codice;
        this.numeroPersone = numeroPersone;
    }

    public Prenotazione() {
        // Necessario per Gson
    }

    public String getNomeFruitore() {
        return nomeFruitore;
    }

    public void setNomeFruitore(String nomeFruitore) {
        this.nomeFruitore = nomeFruitore;
    }

    public int getCodice() {
        return codice;
    }

    public void setCodice(int codice) {
        this.codice = codice;
    }

    public int getNumeroPersone() {
        return numeroPersone;
    }

    public void setNumeroPersone(int numeroPersone) {
        this.numeroPersone = numeroPersone;
    }

     public static int estraiCodice(String fruitore, String org,ArrayList<Prenotazione> prenotazioni) {
        int codice = CODICE_INIZIALE;
        do {
            Random random = new Random();
            codice = CODICE_BASE + random.nextInt(CODICE_RANGE);
        } while (checkCodiceFruitore(codice, org, fruitore, prenotazioni));

        return codice;
    }

    public static boolean checkCodiceFruitore(int codice, String org, String fruitore,ArrayList<Prenotazione> prenotazioni) {
        
        for (Prenotazione p : prenotazioni) {
            if (p.getCodice() == codice && p.getNomeFruitore().equals(fruitore)) {
                return true;
            }
        }
        return false;
    }
     
    

}
