package pl.polsl.service.bayess;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.polsl.alghorithm.SpamDetection;
import pl.polsl.utils.naive_bayes_classifier.BayesClassifier;
import pl.polsl.utils.naive_bayes_classifier.Classification;
import pl.polsl.utils.naive_bayes_classifier.Classifier;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class BayessService implements Serializable, SpamDetection {

    protected static final Double PROBABILITY_LEVEL = 0.001;

    protected final Classifier<String, String> bayes = new BayesClassifier<>();

    @PostConstruct
    void init() {
        learnWhatIsSpam();
    }

    @Override
    public Boolean isThatSpam(final List<String> strings) {
        Collection<Classification<String, String>> classifications = testIncomingFile(strings);
        final float probability = classifications.iterator().next().getProbability();

        return probability <= PROBABILITY_LEVEL;
    }

    public void learnNewSpamPattern(final List<String> strings) {
        List<String> filteredStrings = filterText(strings);
        bayes.learn("spam", filteredStrings);
    }

    public Collection<Classification<String, String>> testIncomingFile(final List<String> strings) {
        return testDetectSpam(bayes, strings);
    }

    public void learnWhatIsSpam() {
        try {
            Files.walk(Paths.get("src/main/resources/static/spam")).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {

                    List<String> stringList = null;

                    try {
                        stringList = Files.readAllLines(filePath);
                        List<String> filteredList = filterText(stringList);

                        bayes.learn("spam", filteredList);
                        System.out.println("Nauczyłem się " + filePath);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Collection<Classification<String, String>> testDetectSpam(final Classifier<String, String> bayes, Collection<String> textToClassify) {
        List<String> filteredText = filterText(textToClassify);

        System.out.println(" --------- ");
        System.out.println("SPRAWDZAM TEKST : ");
        System.out.println(filteredText);
        System.out.println(" --------- ");
        System.out.println(((BayesClassifier<String, String>) bayes).classifyDetailed(filteredText));
        System.out.println(" --------- ");

        return ((BayesClassifier<String, String>) bayes).classifyDetailed(filteredText);
    }

    protected List<String> filterText(final Collection<String> textToClassify) {
        return textToClassify.stream()
                .filter(string -> !string.isEmpty())
                .filter(string -> string.length() != 1)
                .filter(string -> !string.contains("\uFFFC"))
                .collect(Collectors.toList());
    }

    public Classifier<String, String> getBayes() {
        return bayes;
    }

}
