package progettoUnibs.controller;

public class Menu {
    
    private static final String MSG_STRATEGIA_NON_IMPOSTATA = "Strategia non impostata!";
    
    private MenuStrategy strategia;

    public void setStrategia(MenuStrategy strategia) {
        this.strategia = strategia;
    }

    public void eseguiStrategia() {
        if (strategia != null) {
            strategia.mostraMenu();
        } else {
            throw new IllegalStateException(MSG_STRATEGIA_NON_IMPOSTATA);
        }
    }
}
