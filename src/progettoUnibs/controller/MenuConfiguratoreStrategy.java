package progettoUnibs.controller;

import progettoUnibs.model.CorpoDati;

public class MenuConfiguratoreStrategy implements MenuStrategy {
    private GestioneMenuConfiguratore gestioneMenuConfiguratore;
    private CorpoDati dati;

    public MenuConfiguratoreStrategy(GestioneMenuConfiguratore gestioneMenuConfiguratore, CorpoDati dati) {
        this.gestioneMenuConfiguratore = gestioneMenuConfiguratore;
        this.dati = dati;
    }

    @Override
    public void mostraMenu() {
        gestioneMenuConfiguratore.menuConfiguratore(dati);
    }
}
