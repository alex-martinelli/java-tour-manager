package progettoUnibs.controller;

import java.io.*;

import java.util.*;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Credenziali;
import progettoUnibs.model.TipoUtente;
import progettoUnibs.model.Volontario;
import progettoUnibs.view.ViewCredenziali;
import progettoUnibs.view.ViewCredenziali.*;

public class GestioneLogin {

    private static final String SRC_PATH = "src" + File.separator;
    private static final String CONFIGURATORI_SUFFIX = "Configuratori.txt";
    private static final String VOLONTARI_SUFFIX = "Volontari.txt";
    private static final String SPAZIO_SEPARATORE = " ";
    private static final int PRIMO_ACCESSO_INDEX = 2;

    private RepositoryManager repositoryManager;
    private GestioneCredenziali gestioneCredenziali;
    private GestioneCorpoDati gestioneCorpoDati;
    private GestioneMenuConfiguratore gestioneMenuConfiguratore;
    private GestioneMenuFruitore gestioneMenuFruitore;
    private GestioneMenuVolontario gestioneMenuVolontario;
    private ViewCredenziali viewCredenziali = new ViewCredenziali();
    private Map<String, String> credenzialiCache = new HashMap<>();

    public GestioneLogin(RepositoryManager repositoryManager, GestioneMenuFruitore gestioneMenuFruitore) {
        this.repositoryManager = repositoryManager;
        this.gestioneMenuFruitore = gestioneMenuFruitore;
        setGestioneCredenziali(new GestioneCredenziali(repositoryManager));
        setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));
        //setGestioneMenuFruitore(new GestioneMenuFruitore(repositoryManager));
        //gestioneMenuFruitore.setGestioneLogin(this);
        setGestioneMenuVolontario(new GestioneMenuVolontario(repositoryManager));
    }

    public void setGestioneCredenziali(GestioneCredenziali gestioneCred) {
        gestioneCredenziali = gestioneCred;
    }

    public void setGestioneCorpoDati(GestioneCorpoDati gestioneCorpo) {
        gestioneCorpoDati = gestioneCorpo;
    }

    public void setGestioneMenuConfiguratore(GestioneMenuConfiguratore gestioneMenuConf) {
        gestioneMenuConfiguratore = gestioneMenuConf;
    }

    public void setGestioneMenuFruitore(GestioneMenuFruitore gestioneMenuF) {
        gestioneMenuFruitore = gestioneMenuF;
    }

    public void setViewCredenziali(ViewCredenziali viewCred) {
        viewCredenziali = viewCred;
    }

    public void setGestioneMenuVolontario(GestioneMenuVolontario gestioneMenuVol) {
        gestioneMenuVolontario = gestioneMenuVol;
    }

    public void caricaCredenziali(String org, TipoUtente tipoUtente) {
        if (tipoUtente == TipoUtente.CONFIGURATORE)
            credenzialiCache = repositoryManager.getCredenzialiRepository().caricaCredenzialiConfDaFile(org);
        else {
            credenzialiCache = repositoryManager.getCredenzialiRepository().caricaCredenzialiVolDaFile(org);
        }
    }

    public boolean isPrimoAccesso(String org, String username, TipoUtente tipoUtente) {
        File file = new File(
                SRC_PATH + org + File.separator + org
                        + (tipoUtente == TipoUtente.CONFIGURATORE ? CONFIGURATORI_SUFFIX : VOLONTARI_SUFFIX));
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SPAZIO_SEPARATORE);
                if (parts[0].equals(username)) {
                    return Boolean.parseBoolean(parts[PRIMO_ACCESSO_INDEX]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void login(TipoUtente tipoUtente) {

        try {
            Menu menuContext = new Menu();
            
            setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));
            String organizzazione = viewCredenziali.askOrganizzazione();
            CorpoDati dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(organizzazione);
            if (!repositoryManager.getCredenzialiRepository().checkOrganizzazione(organizzazione)) {
                viewCredenziali.showResult(LoginOutcome.ORG_MISSING);
                return;
            }
            String username = viewCredenziali.askUsername();
            String password = viewCredenziali.askPassword();
            caricaCredenziali(organizzazione, tipoUtente);

            if (!credenzialiCache.containsKey(username)) {
                viewCredenziali.showResult(LoginOutcome.USER_NOT_FOUND);
                return;
            }

            if (!credenzialiCache.get(username).equals(password)) {
                viewCredenziali.showResult(LoginOutcome.WRONG_PASS);
                return;
            }

            if (isPrimoAccesso(organizzazione, username, tipoUtente)) {
                cambiaCredenziali(organizzazione, username, tipoUtente);
                return;
            }
            viewCredenziali.showResult(LoginOutcome.SUCCESS);

            if (tipoUtente == TipoUtente.CONFIGURATORE) {
                setGestioneMenuConfiguratore(new GestioneMenuConfiguratore(repositoryManager));
                gestioneCorpoDati.esisteCorpoDati(organizzazione);
                MenuStrategy configuratoreStrategy = new MenuConfiguratoreStrategy(gestioneMenuConfiguratore, dati);
                menuContext.setStrategia(configuratoreStrategy);
                menuContext.eseguiStrategia();
            } else {
                Volontario volontario = new Volontario(new Credenziali(username, password), organizzazione);
                MenuStrategy volontarioStrategy = new MenuVolontarioStrategy(gestioneMenuVolontario, volontario, dati);
                menuContext.setStrategia(volontarioStrategy);
                menuContext.eseguiStrategia();
            }

        } catch (Exception e) {
            e.printStackTrace();
            viewCredenziali.showResult(LoginOutcome.ERROR);
        }
    }

    public void loginFruitore() {
        String username = viewCredenziali.askUsername();
        String password = viewCredenziali.askPassword();

        if (!repositoryManager.getCredenzialiRepository().checkFruitore(username, password)) {
            viewCredenziali.showResult(LoginOutcome.LOGIN_FAIL);
            return;
        }

        viewCredenziali.showResult(LoginOutcome.LOGIN_OK);
        Menu menuContext = new Menu();
        MenuStrategy fruitoreStrategy = new MenuFruitoreStrategy(gestioneMenuFruitore, username);
        menuContext.setStrategia(fruitoreStrategy);
        menuContext.eseguiStrategia();
    }

    public void registrazioneFruitore() {
        String username = viewCredenziali.askUsername();
        if (gestioneCredenziali.controllaUsername(username)) {
            return;
        }
        ;
        String password = viewCredenziali.askPassword();
        viewCredenziali.showResult(LoginOutcome.REGISTER_OK);
        repositoryManager.getCredenzialiRepository().registraFruitore(username, password);
    }

    public void cambiaCredenziali(String organizzazione, String oldUsername, TipoUtente tipoUtente) {

        String newUsername = oldUsername;
        if (tipoUtente == TipoUtente.CONFIGURATORE) {
            do {
                viewCredenziali.showResult(LoginOutcome.DEVI_CAMBIARE_USERNAME);
                newUsername = viewCredenziali.askUsername();
            } while (repositoryManager.getCredenzialiRepository().esisteUsernameGlobale(newUsername));
        }
        viewCredenziali.showResult(LoginOutcome.DEVI_CAMBIARE_PASSWORD);
        String newPassword = viewCredenziali.askPassword();
        repositoryManager.getCredenzialiRepository().modificaCredenziali(organizzazione, oldUsername, newUsername, newPassword, tipoUtente);
        viewCredenziali.showResult(LoginOutcome.CREDENZIALI_AGGIORNATE);
    }

}