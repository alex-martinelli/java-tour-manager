package progettoUnibs.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import progettoUnibs.model.TipoUtente;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.view.OutputPrinter;

public class CredenzialiFileManager implements CredenzialiRepository {

    private static final String CREDENZIALI_FILE_SUFFIX = "Configuratori.txt";
    private static final String VOLONTARI_FILE_SUFFIX = "Volontari.txt";
    private static final String ERRORE_SCRITTURA_FILE = "Errore nella scrittura del file: ";
    private static final String FRUITORI_FILE = "fruitori.txt";
    private static final String SRC_PATH = "src" + File.separator;
    private static final String SPAZIO_SEPARATORE = " ";
    private static final String FALSE_DEFAULT = " false";
    private static final String WHITESPACE_PATTERN = "\\s+";

    private OutputPrinter outputPrinter = new OutputPrinter();

    public Map<String, String> caricaCredenzialiDaFile(String filePath) {
        Map<String, String> credenziali = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return credenziali;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                 line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(SPAZIO_SEPARATORE);
                 if (parts.length < 2) continue;
                credenziali.put(parts[0], parts[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credenziali;
    }

    public Map<String, String> caricaCredenzialiConfDaFile(String org) {
        String filePath = FileUtils.buildFilePath(org, org + CREDENZIALI_FILE_SUFFIX);
        return caricaCredenzialiDaFile(filePath);
    }

    public Map<String, String> caricaCredenzialiVolDaFile(String org) {
        String filePath = FileUtils.buildFilePath(org, org + VOLONTARI_FILE_SUFFIX);
        return caricaCredenzialiDaFile(filePath);
    }

    public boolean checkOrganizzazione(String organizzazione) {
        File file = new File(SRC_PATH + organizzazione);
        return file.exists();
    }

    public void modificaCredenziali(String org, String username, String newUsername, String newPassword,
            TipoUtente tipo) {
        List<String> lines = new ArrayList<>();
        String filePath = FileUtils.buildFilePath(org,
                org + (tipo == TipoUtente.CONFIGURATORE ? CREDENZIALI_FILE_SUFFIX : VOLONTARI_FILE_SUFFIX));
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SPAZIO_SEPARATORE);
                if (parts[0].equals(username)) {
                    lines.add(newUsername + SPAZIO_SEPARATORE + newPassword + FALSE_DEFAULT);
                } else {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registraFruitore(String username, String password) {
        String filePath = SRC_PATH + FRUITORI_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
            writer.write(username + SPAZIO_SEPARATORE + password);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_SCRITTURA_FILE + e.getMessage());
        }
    }

    public boolean checkFruitore(String username, String password) {
        Map<String, String> credenziali = new HashMap<>();
        credenziali = caricaCredenzialiDaFile(SRC_PATH + FRUITORI_FILE);
        if (credenziali.containsKey(username)) {
            String passwordMemorizzata = credenziali.get(username);
            return password.equals(passwordMemorizzata);
        } else {
            return false;
        }
    }

    public boolean esisteUsernameGlobale(String username) {
        Path startPath = Paths.get(SRC_PATH);
        try (Stream<Path> files = Files.walk(startPath)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String nome = path.getFileName().toString();
                        return (nome.endsWith(CREDENZIALI_FILE_SUFFIX) || nome.endsWith(VOLONTARI_FILE_SUFFIX)
                                || nome.equals(FRUITORI_FILE));
                    })
                    .anyMatch(path -> {
                        try (Stream<String> righe = Files.lines(path)) {
                            return righe
                                    .map(String::trim)
                                    .filter(riga -> !riga.isEmpty())
                                    .map(riga -> riga.split(WHITESPACE_PATTERN)[0])
                                    .anyMatch(name -> name.equalsIgnoreCase(username));
                        } catch (IOException e) {
                            return false;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
