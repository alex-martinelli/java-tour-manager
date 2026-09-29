package progettoUnibs.view;

public class ViewCredenziali {
   
    private static final String MESSAGGIO_ORGANIZZAZIONE = "Inserisci organizzazione:";
    private static final String MESSAGGIO_USERNAME = "Inserisci username:";
    private static final String MESSAGGIO_PASSWORD = "Inserisci password:";
    private static final String MSG_ACCESSO_RIUSCITO = "Accesso riuscito.";
    private static final String MSG_ORGANIZZAZIONE_NON_ESISTENTE = "Organizzazione non esistente.";
    private static final String MSG_UTENTE_NON_REGISTRATO = "Utente non registrato.";
    private static final String MSG_PASSWORD_ERRATA = "Password errata.";
    private static final String MSG_DEVI_CAMBIARE_CREDENZIALI = "Devi cambiare le tue credenziali.";
    private static final String MSG_ERRORE_LOGIN = "Errore durante il login.";
    private static final String MSG_ACCESSO_SUCCESSO = "Accesso avvenuto con successo.";
    private static final String MSG_CREDENZIALI_ERRATE = "Credenziali errate.";
    private static final String MSG_REGISTRAZIONE_SUCCESSO = "Registrazione avvenuta con successo.";
    private static final String MSG_USERNAME_ESISTENTE = "Username già esistente. Riprova.";
    private static final String MSG_DEVI_CAMBIARE_USERNAME = "Devi cambiare username.";
    private static final String MSG_DEVI_CAMBIARE_PASSWORD = "Devi cambiare password.";
    private static final String MSG_CREDENZIALI_AGGIORNATE = "Credenziali aggiornate.";
    private static final String MSG_OPERAZIONE_NON_RICONOSCIUTA = "Operazione non riconosciuta.";

    public String askOrganizzazione() {
        return InputReader.leggiStringaNonVuota(MESSAGGIO_ORGANIZZAZIONE);
    }

    public String askUsername() {
        return InputReader.leggiStringaNonVuota(MESSAGGIO_USERNAME);
    }

    public String askPassword() {
        return InputReader.leggiStringaNonVuota(MESSAGGIO_PASSWORD);
    }

    public enum LoginOutcome {
        SUCCESS, ORG_MISSING, USER_NOT_FOUND,
        WRONG_PASS, MUST_CHANGE_PASS, ERROR, LOGIN_OK, LOGIN_FAIL,
        REGISTER_OK, USERNAME_EXISTS, DEVI_CAMBIARE_USERNAME, DEVI_CAMBIARE_PASSWORD,CREDENZIALI_AGGIORNATE
    }

    public void showResult(LoginOutcome res) {
        switch (res) {
            case SUCCESS -> System.out.println(MSG_ACCESSO_RIUSCITO);
            case ORG_MISSING -> System.out.println(MSG_ORGANIZZAZIONE_NON_ESISTENTE);
            case USER_NOT_FOUND -> System.out.println(MSG_UTENTE_NON_REGISTRATO);
            case WRONG_PASS -> System.out.println(MSG_PASSWORD_ERRATA);
            case MUST_CHANGE_PASS -> System.out.println(MSG_DEVI_CAMBIARE_CREDENZIALI);
            case ERROR -> System.out.println(MSG_ERRORE_LOGIN);
            case LOGIN_OK -> System.out.println(MSG_ACCESSO_SUCCESSO);
            case LOGIN_FAIL -> System.out.println(MSG_CREDENZIALI_ERRATE);
            case REGISTER_OK -> System.out.println(MSG_REGISTRAZIONE_SUCCESSO);
            case USERNAME_EXISTS -> System.out.println(MSG_USERNAME_ESISTENTE);
            case DEVI_CAMBIARE_USERNAME -> System.out.println(MSG_DEVI_CAMBIARE_USERNAME);
            case DEVI_CAMBIARE_PASSWORD -> System.out.println(MSG_DEVI_CAMBIARE_PASSWORD);
            case CREDENZIALI_AGGIORNATE -> System.out.println(MSG_CREDENZIALI_AGGIORNATE);
            default -> System.out.println(MSG_OPERAZIONE_NON_RICONOSCIUTA);
        }
    }

}