package progettoUnibs.controller;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Volontario;

public class MenuVolontarioStrategy implements MenuStrategy {
    private GestioneMenuVolontario gestioneMenuVolontario;
    private Volontario volontario;
    private CorpoDati dati;

    public MenuVolontarioStrategy(GestioneMenuVolontario gestioneMenuVolontario, Volontario volontario, CorpoDati dati) {
        this.gestioneMenuVolontario = gestioneMenuVolontario;
        this.volontario = volontario;
        this.dati = dati;
    }

    @Override
    public void mostraMenu() {
        gestioneMenuVolontario.menuVolontario(dati, volontario);
    }
}
