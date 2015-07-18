package pl.polsl.service.new_bayess;

public class Word {
    protected String word;    // The String itself
    protected int countBad;   // The total times it appears in "bad" messages
    protected int countGood;  // The total times it appears in "good" messages
    protected float rBad;     // bad count / total bad words
    protected float rGood;    // good count / total good words
    protected float pSpam;    // probability this word is Spam

    // Create a word, initialize all vars to 0
    public Word(final String s) {
        word = s;
        countBad = 0;
        countGood = 0;
        rBad = 0.0f;
        rGood = 0.0f;
        pSpam = 0.0f;
    }

    // Increment bad counter
    public void countBad() {
        countBad++;
    }

    public void countGood() {
        countBad++;
    }

    // Implement bayes rules to compute how likely this word is "spam"
    public void finalizeProb() {
        if (rGood + rBad > 0) pSpam = rBad / (rBad + rGood);
        if (pSpam < 0.01f) pSpam = 0.01f;
        else if (pSpam > 0.99f) pSpam = 0.99f;
    }

    // The â€œinterestingâ€ rating for a word is
    // How different from 0.5 it is
    public float interesting() {
        return Math.abs(0.5f - pSpam);
    }
}
