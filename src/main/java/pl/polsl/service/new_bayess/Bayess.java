package pl.polsl.service.new_bayess;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class Bayess {

    protected HashMap hams = new HashMap();
    protected HashMap spams = new HashMap();

    @PostConstruct
    public void init() {
        train();
    }

    public void train() {
        trainHam();
        trainSpam();
    }

    public void trainHam() {
        try {
            Files.walk(Paths.get("src/main/resources/static/ham_new"))
                    .filter(path -> Files.isRegularFile(path))
                    .map(this::getLines)
                    .map(this::trimToArrayTerms)
                    .forEach(terms -> analyseWordsAsHam(terms, hams));

            calculateProbability(hams);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trainSpam() {
        try {
            Files.walk(Paths.get("src/main/resources/static/spam_new"))
                    .parallel()
                    .filter(path -> Files.isRegularFile(path))
                    .map(this::getLines)
                    .map(this::trimToArrayTerms)
                    .forEach(terms -> analyseWordsAsSpam(terms, spams));

            calculateProbability(spams);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void calculateProbability(final HashMap words) {
        for (final Object o : words.values()) {
            Word word = (Word) o;
            word.finalizeProb();
        }
    }

    protected final List<String> getLines(final Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Cannot get lines from current path:" + path);
    }

    protected List<String> trimToArrayTerms(final List<String> allLines) {
        List<String> words = new ArrayList<>();
        allLines.parallelStream().forEach(line -> {
            List<String> listOfWordsForLine = Arrays.asList(line.split("\\W"));
            words.addAll(listOfWordsForLine);
        });

        return words;
    }

    //TODO ugenerycznic te dwie metody
    protected void analyseWordsAsSpam(final List<String> terms, final HashMap wordsMap) {
        terms.parallelStream().forEach(term -> {
            if (!isEmpty(term)) {

                Word word;

                if (wordsMap.containsKey(term)) {
                    word = (Word) wordsMap.get(term);
                    word.countBad();
                } else {
                    word = new Word(term);
                    wordsMap.put(term, word);
                    word.countBad();
                }

            }
        });

    }

    protected void analyseWordsAsHam(final List<String> terms, final HashMap wordsMap) {
        terms.parallelStream().forEach(term -> {
            if (!isEmpty(term)) {

                Word word;

                if (wordsMap.containsKey(term)) {
                    word = (Word) wordsMap.get(term);
                    word.countGood();
                } else {
                    word = new Word(term);
                    wordsMap.put(term, word);
                    word.countGood();
                }

            }
        });

    }

    protected void countAsSpam(final List<Word> words) {
        words.parallelStream().forEach(Word::countBad);
    }

    protected void countAsHam(final List<Word> words) {
        words.parallelStream().forEach(Word::countGood);
    }

}
