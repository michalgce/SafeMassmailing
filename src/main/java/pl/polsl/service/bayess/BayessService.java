package pl.polsl.service.bayess;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BayessService {
    // How to split the String into  tokens
    protected String splitregex;
    // Regex to eliminate junk (although we really should welcome the junk)
    protected Pattern wordregex;
    protected DB dbSpam;
    protected DB dbHam;
    protected DB analazyedWordDB;
    protected HTreeMap<Long, String> spamWordMap;
    protected HTreeMap<Long, String> hamWordMap;
    protected HTreeMap<String, Word> wordAnalyzedMap;

    @PostConstruct
    public void init() {
        dbSpam = DBMaker
                .fileDB((Paths.get("dbSpam.bin")).toFile())
                .transactionDisable()
                .closeOnJvmShutdown()
                .make();
        dbHam = DBMaker
                .fileDB((Paths.get("dbHam.bin")).toFile())
                .transactionDisable()
                .closeOnJvmShutdown()
                .make();
        analazyedWordDB = DBMaker
                .fileDB((Paths.get("analyzedWord.bin")).toFile())
                .transactionDisable()
                .closeOnJvmShutdown()
                .make();
        splitregex = "\\W";
        wordregex = Pattern.compile("\\w+");
        //spamWordMap = dbSpam.hashMapCreate("spamWordMap").makeOrGet();
        //hamWordMap = dbHam.hashMapCreate("hamWordMap").makeOrGet();
        wordAnalyzedMap = dbSpam.hashMapCreate("wordAnalyzedMap").makeOrGet();
        //wordAnalyzedMap.clear();
        //train();
    }

    public void train() {
        trainSpam("/static/spam");
        trainGood("/static/ham");
        finalizeTraining();
        //System.out.println("asd");
    }

    public void trainSpam(final String directory) {

       /*LazyFileReader fileReader = new LazyFileReader();
        try {
            Files.list(Paths.get(directory)).forEach(directoryEntry -> {
                try {
                    Files.list(directoryEntry).forEach(filePath -> fileReader.buildArrayWord(filePath.toString(), spamWordMap));
                } catch (IOException e) {
                    e.printStackTrace();
                }}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // Read the content and break up into words
        final int[] spamTotal = {0};//tokenizer.countTokens(); // How many words total
        // For every word token
        System.out.println("Ropoczynam analyze slow spamowych.");
        spamWordMap.forEach((index, token) -> {
            Matcher m = wordregex.matcher(token);
            if (m.matches()) {
                spamTotal[0]++;
                // If it exists in the HashMap already
                // Increment the count
                if (wordAnalyzedMap.containsKey(token)) {
                    Word w = wordAnalyzedMap.get(token);
                    w.countBad();
                    wordAnalyzedMap.put(token, w);
                    // Otherwise it's a new word so add it
                } else {
                    Word w = new Word(token);
                    w.countBad();
                    wordAnalyzedMap.put(token, w);
                }
            }
        });

        // Go through all the words and divide
        // by total word
        final int size = wordAnalyzedMap.size();
        wordAnalyzedMap.forEach((s, word) -> {
            word.calcBadProb(size);
            wordAnalyzedMap.put(s, word);
        });
    }

    public void trainGood(final String directory) {

       /*LazyFileReader fileReader = new LazyFileReader();
        try {
            Files.list(Paths.get(directory)).forEach(directoryEntry -> {
                        try {
                            Files.list(directoryEntry).forEach(filePath -> fileReader.buildArrayWord(filePath.toString(), hamWordMap));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // Read the content and break up into words
        final int[] hamTotal = {0};//tokenizer.countTokens(); // How many words total
        // For every word token
        System.out.println("Ropoczynam analyze slow hamowych.");
        hamWordMap.forEach((index, token) -> {
            Matcher m = wordregex.matcher(token);
            if (m.matches()) {
                hamTotal[0]++;
                // If it exists in the HashMap already
                // Increment the count
                if (wordAnalyzedMap.containsKey(token)) {
                    Word w = wordAnalyzedMap.get(token);
                    w.countGood();
                    wordAnalyzedMap.put(token, w);
                    // Otherwise it's a new word so add it
                } else {
                    Word w = new Word(token);
                    w.countGood();
                    wordAnalyzedMap.put(token, w);
                }
            }
        });

        // Go through all the words and divide
        // by total words
        final int size = wordAnalyzedMap.size();
        wordAnalyzedMap.forEach((s, word) -> {
            word.calcGoodProb(size);
            wordAnalyzedMap.put(s, word);
        });
    }

    // For every word, calculate the Spam probability
    public void finalizeTraining() {
        wordAnalyzedMap.forEach((s, word) -> {
            word.finalizeProb();
            wordAnalyzedMap.put(s, word);
        });
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
                if (wordAnalyzedMap.containsKey(s)) {
                    w = (Word) wordAnalyzedMap.get(s);
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

    // Display info about the words in the HashMap
    public void displayStats() {
        Iterator iterator = wordAnalyzedMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Word word = (Word) wordAnalyzedMap.get(key);
            if (word != null) {
                //System.out.println(key + " pBad: " + word.getPBad() + " pGood: " + word.getPGood() + " pSpam: " + word.getPSpam());
                System.out.println(key + " " + word.getPSpam());
            }
        }
    }

}
