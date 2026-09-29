package progettoUnibs.controller;

public class RepositoryManager {
    private CorpoDatiRepository corpoDatiRepository;
    private VolontariRepository volontariRepository;
    private AttivitaRepository attivitaRepository;
    private DisponibilitaRepository disponibilitaRepository;
    private CredenzialiRepository credenzialiRepository;
    private PrenotazioniRepository prenotazioniRepository;

    public RepositoryManager(AttivitaRepository attivitaRepository,
                             CorpoDatiRepository corpoDatiRepository,
                             DisponibilitaRepository disponibilitaRepository,
                             CredenzialiRepository credenzialiRepository,
                             VolontariRepository volontariRepository,
                             PrenotazioniRepository prenotazioniRepository) {
        this.corpoDatiRepository = corpoDatiRepository;
        this.volontariRepository = volontariRepository;
        this.attivitaRepository = attivitaRepository;
        this.disponibilitaRepository = disponibilitaRepository;
        this.credenzialiRepository = credenzialiRepository;
        this.prenotazioniRepository = prenotazioniRepository;
    }

    public CorpoDatiRepository getCorpoDatiRepository() {
        return corpoDatiRepository;
    }

    public VolontariRepository getVolontariRepository() {
        return volontariRepository;
    }

    public AttivitaRepository getAttivitaRepository() {
        return attivitaRepository;
    }

    public DisponibilitaRepository getDisponibilitaRepository() {
        return disponibilitaRepository;
    }

    public CredenzialiRepository getCredenzialiRepository() {
        return credenzialiRepository;
    }

    public PrenotazioniRepository getPrenotazioniRepository() {
        return prenotazioniRepository;
    }
}
