package progettoUnibs.controller;

import progettoUnibs.model.CorpoDati;

public interface CorpoDatiRepository {
    void salvaFileCorpoDati(CorpoDati corpo);
    boolean isDataCreated(String org);
    CorpoDati leggiCorpoDatiDaFile(String organizzazione);
}
