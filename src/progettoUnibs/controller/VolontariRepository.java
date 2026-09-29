package progettoUnibs.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import progettoUnibs.model.CorpoDati;

public interface VolontariRepository {
    Set<String> leggiVolontariDaTxt(String org);
    Set<String> leggiVolontariDaJson(String organizzazione);
    void scriviListaVolontari(String organizzazione, String nomeVolontario);
    void rimuoviVolontarioDaFile(String nickname, String organizzazione);
    Map<String, List<String>> costruisciMappaVolontari(CorpoDati dati);
}
