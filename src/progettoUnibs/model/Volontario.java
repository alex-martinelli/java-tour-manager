package progettoUnibs.model;

public class Volontario {
    private Credenziali credenziali;
    private String organizzazione;

    public Volontario(Credenziali credenziali, String organizzazione) {
        this.credenziali = credenziali;
        this.organizzazione = organizzazione;
    }

    public String getOrganizzazione() {
        return organizzazione;
    }

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
    }

    public void setOrganizzazione(String organizzazione) {
        this.organizzazione = organizzazione;
    }
}
