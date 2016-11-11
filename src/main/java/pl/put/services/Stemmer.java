package pl.put.services;

import pl.put.model.Stemmable;

import java.util.Collection;

/**
 * Author: Krystian Świdurski
 */
public class Stemmer<T extends Stemmable> {

    private String charsToRemove;
    StemmerEngine stemmerEngine = new StemmerEngine();


    public Stemmer() {
        this("!@#$%^&*()-+=|\\[]{};:|\"<>,.?/~`");
    }

    public Stemmer(String charsToRemove) {
        this.charsToRemove = charsToRemove;
    }

    public String run(String string) {
        if (string == null || string.trim().length() == 0) {
            return "";
        }
        return stemmerEngine.stem(removeChars(string.toLowerCase()));
    }

    private String removeChars(String string) {
        if (charsToRemove == null) {
            return string;
        }

        for (int i = 0; i < charsToRemove.length(); i++) {
            string = string.replace(charsToRemove.substring(i, i + 1), "");
        }
        return string;
    }

    public void run(T stemmable) {
        if (stemmable != null) {
            stemmable.stem(this);
        }
    }

    public void run(Collection<T> list) {
        if (list != null) {
            list.forEach(this::run);
        }
    }
}
