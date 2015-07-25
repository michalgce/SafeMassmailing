package pl.polsl.service.bayess;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class BayessService {

    // A HashMap to keep track of all words
    HashMap words;
    // How to split the String into  tokens
    String splitregex;
    // Regex to eliminate junk (although we really should welcome the junk)
    Pattern wordregex;
    protected ConcurrentHashMap<String, Word> hams = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, Word> spams = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        words = new HashMap();
        splitregex = "\\W";
        wordregex = Pattern.compile("\\w+");
        train();
    }

    public void train() {
        //TODO parralel it
        //trainHam();
        //trainSpam();
        try {
            trainSpam("/Users/rastabandita/Projects/SafeMassmailing/src/main/resources/static/spam_new/spam.txt");
            trainSpam("/Users/rastabandita/Projects/SafeMassmailing/src/main/resources/static/spam_new/spam2.txt");
            trainGood("/Users/rastabandita/Projects/SafeMassmailing/src/main/resources/static/ham_new/good.txt");
            trainGood("/Users/rastabandita/Projects/SafeMassmailing/src/main/resources/static/ham_new/ham.txt");
            trainGood("/Users/rastabandita/Projects/SafeMassmailing/src/main/resources/static/ham_new/ham2.txt");
            finalizeTraining();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trainSpam(String file) throws IOException {
        A2ZFileReader fr = new A2ZFileReader(file);

        // Read the content and break up into words
        String content = fr.getContent();
        String[] tokens = content.split(splitregex);
        int spamTotal = 0;//tokenizer.countTokens(); // How many words total

        // For every word token
        for (int i = 0; i < tokens.length; i++) {
            String word = tokens[i].toLowerCase();
            Matcher m = wordregex.matcher(word);
            if (m.matches()) {
                spamTotal++;
                // If it exists in the HashMap already
                // Increment the count
                if (words.containsKey(word)) {
                    Word w = (Word) words.get(word);
                    w.countBad();
                    // Otherwise it's a new word so add it
                } else {
                    Word w = new Word(word);
                    w.countBad();
                    words.put(word,w);
                }
            }
        }

        // Go through all the words and divide
        // by total words
        Iterator iterator = words.values().iterator();
        while (iterator.hasNext()) {
            Word word = (Word) iterator.next();
            word.calcBadProb(spamTotal);
        }
    }

    public void trainGood(String file) throws IOException {
        A2ZFileReader fr = new A2ZFileReader(file);

        // Read the content and break up into words
        String content = fr.getContent();
        String[] tokens = content.split(splitregex);
        int goodTotal = 0;//tokenizer.countTokens(); // How many words total

        // For every word token
        for (int i = 0; i < tokens.length; i++) {
            String word = tokens[i].toLowerCase();
            Matcher m = wordregex.matcher(word);
            if (m.matches()) {
                goodTotal++;
                // If it exists in the HashMap already
                // Increment the count
                if (words.containsKey(word)) {
                    Word w = (Word) words.get(word);
                    w.countGood();
                    // Otherwise it's a new word so add it
                } else {
                    Word w = new Word(word);
                    w.countGood();
                    words.put(word,w);
                }
            }
        }

        // Go through all the words and divide
        // by total words
        Iterator iterator = words.values().iterator();
        while (iterator.hasNext()) {
            Word word = (Word) iterator.next();
            word.calcGoodProb(goodTotal);
        }
    }

    // For every word, calculate the Spam probability
    public void finalizeTraining() {
        Iterator iterator = words.values().iterator();
        while (iterator.hasNext()) {
            Word word = (Word) iterator.next();
            word.finalizeProb();
        }
    }

    public Boolean analyze(String stuff) {

        // Create an arraylist of 15 most "interesting" words
        // Words are most interesting based on how different their Spam probability is from 0.5
        ArrayList interesting = new ArrayList();

        // For every word in the String to be analyzed
        String[] tokens = stuff.split("\\W");

        for (final String token : tokens) {
            String s = token.toLowerCase();
            Matcher m = wordregex.matcher(s);
            if (m.matches()) {

                Word w;

                // If the String is in our HashMap get the word out
                if (words.containsKey(s)) {
                    w = (Word) words.get(s);
                    // Otherwise, make a new word with a Spam probability of 0.4;
                } else {
                    w = new Word(s);
                    w.setPSpam(0.4f);
                }

                // We will limit ourselves to the 15 most interesting word
                int limit = 15;
                // If this list is empty, then add this word in!
                if (interesting.isEmpty()) {
                    interesting.add(w);
                    // Otherwise, add it in sorted order by interesting level
                } else {
                    for (int j = 0; j < interesting.size(); j++) {
                        // For every word in the list already
                        Word nw = (Word) interesting.get(j);
                        // If it's the same word, don't bother
                        if (w.getWord().equals(nw.getWord())) {
                            break;
                            // If it's more interesting stick it in the list
                        } else if (w.interesting() > nw.interesting()) {
                            interesting.add(j, w);
                            break;
                            // If we get to the end, just tack it on there
                        } else if (j == interesting.size() - 1) {
                            interesting.add(w);
                        }
                    }
                }

                // If the list is bigger than the limit, delete entries
                // at the end (the more "interesting" ones are at the
                // start of the list
                while (interesting.size() > limit) interesting.remove(interesting.size() - 1);

            }
        }

        // Apply Bayes' rule (via Graham)
        float pposproduct = 1.0f;
        float pnegproduct = 1.0f;
        // For every word, multiply Spam probabilities ("Pspam") together
        // (As well as 1 - Pspam)
        for (int i = 0; i < interesting.size(); i++) {
            Word w = (Word) interesting.get(i);
            //System.out.println(w.getWord() + " " + w.getPSpam());
            pposproduct *= w.getPSpam();
            pnegproduct *= (1.0f - w.getPSpam());
        }

        // Apply formula
        float pspam = pposproduct / (pposproduct + pnegproduct);
        System.out.println("\nSpam rating: " + pspam);

        // If the computed value is great than 0.9 we have a Spam!!
        if (pspam > 0.9f) return true;
        else return false;

    }

    public Boolean analyze2(final String message) {
        words = new HashMap<>();

        // Create an arraylist of 15 most "interesting" words
        List<Word> interesting = Lists.newArrayList();


        List<String> terms = Lists.newArrayList(message.split("\\W"));
        terms.forEach(term -> {
            String lowerCaseTerm = term.toLowerCase();
            Matcher matcher = wordregex.matcher(lowerCaseTerm);
            if (matcher.matches()) {
                Word word;

                if (words.containsKey(lowerCaseTerm)) {
                    word = (Word) words.get(lowerCaseTerm);
                } else {
                    word = new Word(lowerCaseTerm);
                    word.setPSpam(0.4f);
                }

                final int limit = 15;
                if (interesting.isEmpty()) {
                    interesting.add(word);
                } else {
                    for (int j = 0; j < interesting.size(); j++) {
                        // For every word in the list already
                        Word nw = (Word) interesting.get(j);
                        // If it's the same word, don't bother
                        if (word.getWord().equals(nw.getWord())) {
                            break;
                            // If it's more interesting stick it in the list
                        } else if (word.interesting() > nw.interesting()) {
                            interesting.add(j,word);
                            break;
                            // If we get to the end, just tack it on there
                        } else if (j == interesting.size()-1) {
                            interesting.add(word);
                        }
                    }
                }

                // If the list is bigger than the limit, delete entries
                // at the end (the more "interesting" ones are at the
                // start of the list
                while (interesting.size() > limit) interesting.remove(interesting.size()-1);

            }

        });

        // Apply Bayes' rule (via Graham)
        final float[] pposproduct = {1.0f};
        final float[] pnegproduct = {1.0f};
        // For every word, multiply Spam probabilities ("Pspam") together
        // (As well as 1 - Pspam)
        interesting.forEach( interestingEntry -> {
            pposproduct[0] *= interestingEntry.getPSpam();
            pnegproduct[0] *= (1.0f - interestingEntry.getPSpam());
        });

        // Apply formula
        float pspam = pposproduct[0] / (pposproduct[0] + pnegproduct[0]);
        System.out.println("\nSpam rating: " + pspam);

        // If the computed value is great than 0.9 we have a Spam!!
        return pspam > 0.9f;
    }

    // Display info about the words in the HashMap
    public void displayStats() {
        Iterator iterator = words.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Word word = (Word) words.get(key);
            if (word != null) {
                //System.out.println(key + " pBad: " + word.getPBad() + " pGood: " + word.getPGood() + " pSpam: " + word.getPSpam());
                System.out.println(key + " " + word.getPSpam());
            }
        }
    }

    protected void trainHam() {
        try {
            Files.walk(Paths.get("src/main/resources/static/ham_new"))
                    .parallel()
                    .filter(path -> Files.isRegularFile(path))
                    .map(this::getLines)
                    .map(this::trimToArrayTerms)
                    .forEach(terms -> analyseWordsAsHam(terms, hams));

            calculateProbability(hams);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void trainSpam() {
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

    protected void calculateProbability(final ConcurrentHashMap words) {
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
    protected void analyseWordsAsSpam(final List<String> terms, final ConcurrentHashMap<String, Word> wordsMap) {
        terms.parallelStream().forEach(term -> {
            if (!isEmpty(term)) {

                Word word;

                if (wordsMap.containsKey(term)) {
                    word = wordsMap.get(term);
                } else {
                    word = new Word(term);
                    wordsMap.put(term, word);
                }

                word.countBad();

            }
        });

    }

    protected void analyseWordsAsHam(final List<String> terms, final ConcurrentHashMap<String, Word> wordsMap) {
        terms.parallelStream().forEach(term -> {
            if (!isEmpty(term)) {
                Word word;
                if (wordsMap.containsKey(term)) {
                    word = wordsMap.get(term);
                } else {
                    word = new Word(term);
                    wordsMap.put(term, word);
                }
                word.countGood();
            }
        });

    }
}
