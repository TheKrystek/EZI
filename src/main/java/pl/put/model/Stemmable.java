package pl.put.model;

import pl.put.services.Stemmer;

/**
 * Author: Krystian Świdurski
 */
public interface Stemmable {
    void stem(Stemmer stemmer);
}
