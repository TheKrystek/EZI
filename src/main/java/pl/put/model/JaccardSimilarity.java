package pl.put.model;

import pl.put.services.TFIDF;

import java.util.Map;

/**
 * Author: Krystian Świdurski
 */
public class JaccardSimilarity implements SimilarityModel {
    private TFIDF tfidf;

    @Override
    public void setTFIDF(TFIDF tfidf) {
        this.tfidf = tfidf;
    }

    @Override
    public double calculate(Document d1, Document d2) {
        double union = 0;
        double intersection = 0;
        for (Keyword keyword : tfidf.getKeywords()) {
            double d1TF = tfidf.getTermFrequency(keyword, d1);
            double d2TF = tfidf.getTermFrequency(keyword, d2);

            intersection += (d1TF > 0 && d2TF > 0) ? 1 : 0;
            union += (d1TF > 0 || d2TF > 0) ? 1 : 0;
        }
        return union == 0 ? 0 : intersection / union;
    }

    @Override
    public double calculate(Query query, Document document) {
        double union = 0;
        double intersection = 0;
        Map<Keyword, Double> termFrequency = tfidf.getTermFrequency(query);
        for (Keyword keyword : tfidf.getKeywords()) {
            double queryTF = termFrequency.get(keyword);
            double documentTF = tfidf.getTermFrequency(keyword, document);

            intersection += (queryTF > 0 && documentTF > 0) ? 1 : 0;
            union += (queryTF > 0 || documentTF > 0) ? 1 : 0;
        }
        return union == 0 ? 0 : intersection / union;
    }
}
