package progettoUnibs.controller;

public class MenuFruitoreStrategy implements MenuStrategy {
    private GestioneMenuFruitore gestioneMenuFruitore;
    private String fruitore;

    public MenuFruitoreStrategy(GestioneMenuFruitore gestioneMenuFruitore, String fruitore) {
        this.gestioneMenuFruitore = gestioneMenuFruitore;
        this.fruitore = fruitore;
    }

    @Override
    public void mostraMenu() {
        gestioneMenuFruitore.menuFruitore(fruitore);
    }
}
