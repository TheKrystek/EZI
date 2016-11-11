package pl.put.model;

import pl.put.services.TFIDF;

/**
 * Author: Krystian Świdurski
 */
public interface SimilarityModel {

    void setTFIDF(TFIDF tfidf);

    double calculate(Document d1, Document d2);
    double calculate(Query query, Document document);
}
