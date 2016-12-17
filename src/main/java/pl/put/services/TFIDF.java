package pl.put.services;

import lombok.Getter;
import pl.put.model.*;
import pl.put.utils.DoubleKeyMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Krystian Świdurski
 */
public class TFIDF {
    @Getter
    private final Documents documents;
    @Getter
    private final Keywords keywords;

    private final DoubleKeyMap<Keyword, Document, Double> termDocumentCount = new DoubleKeyMap<>();
    private final DoubleKeyMap<Document, Keyword, Double> documentTermCount = new DoubleKeyMap<>();
    private final DoubleKeyMap<Keyword, Document, Double> TF = new DoubleKeyMap<>();
    private final Map<Keyword, Double> IDF = new HashMap<>();

    public TFIDF(Document document, Keywords keywords) {
        this(new Documents(document), keywords);
    }


    public TFIDF(Documents documents, Keywords keywords) {
        this.documents = documents;
        this.keywords = keywords;
        run();
    }


    //<editor-fold desc="Public methods">
    public void run() {
        for (Document document : documents) {
            for (Keyword keyword : keywords) {
                saveKeywordsFrequency(keyword, document);
            }
        }
        for (Document document : documents) {
            calcTermFrequency(document, keywords);
        }
        calculateInverseDocumentFrequency(keywords);
    }


    public double getTFIDF(Keyword keyword, Document document) {
        return getTermFrequency(keyword, document) * getInverseDocumentFrequency(keyword);
    }

    public double getTermFrequency(Keyword keyword, Document document) {
        if (TF.contains(keyword, document)) {
            return TF.get(keyword, document).doubleValue();
        }
        return 0;
    }

    public double getInverseDocumentFrequency(Keyword keyword) {
        return IDF.getOrDefault(keyword, 0.0);
    }

    //<editor-fold desc="Queries">
    public Map<Keyword, Double> getTermFrequency(Query query) {
        HashMap<Keyword, Double> map = new HashMap<>();
        for (Keyword keyword : keywords) {
            map.put(keyword, calcKeywordsOccurrencesInDocument(keyword, query));
        }
        return map;
    }

    public Map<Keyword, Double> getTFIDF(Query query) {
        Map<Keyword, Double> termFrequency = getTermFrequency(query);
        HashMap<Keyword, Double> map = new HashMap<>();
        for (Keyword keyword : keywords) {
            double tf = 0.0;
            if (termFrequency.containsKey(keyword)) {
                tf = termFrequency.get(keyword);
            }
            double idf = getInverseDocumentFrequency(keyword);
            map.put(keyword, tf * idf);
        }
        return map;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Basic methods">
    private double saveKeywordsFrequency(Keyword keyword, Document document) {
        double count = calcKeywordsOccurrencesInDocument(keyword, document);
        termDocumentCount.put(keyword, document, count);
        documentTermCount.put(document, keyword, count);
        return count;
    }


    private double calcKeywordsOccurrencesInDocument(Keyword keyword, Document document) {
        List<String> list = Arrays.asList(document.getStemmedContent().split(" "));
        return list.parallelStream().filter(p -> p.equals(keyword.getStemmedValue())).count();
    }
    //</editor-fold>

    //<editor-fold desc="TF - Term frequency">
    private void calcTermFrequency(Document document, Keywords keywords) {
        double sumOfTerms = calculateNumberOfKeywordsInDocument(document);
//        double sumOfTerms = calculateTheMostCommonKeywordInDocument(document);
        for (Keyword keyword : keywords) {
            calcTermFrequency(document, keyword, sumOfTerms);
        }
    }

    private double calcTermFrequency(Document document, Keyword keyword, double sumOfTerms) {
        double frequency = documentTermCount.get(document, keyword).doubleValue();
        double tf = 0;
        if (sumOfTerms > 0) {
            tf = frequency / sumOfTerms;
        }
        TF.put(keyword, document, tf);
        return tf;
    }

    private double calculateTheMostCommonKeywordInDocument(Document document) {
        Map<Keyword, Double> map = documentTermCount.get(document);
        double max = 0;
        for (Map.Entry<Keyword, Double> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        return max;
    }

    private double calculateNumberOfKeywordsInDocument(Document document) {
        Map<Keyword, Double> map = documentTermCount.get(document);
        double sum = 0;
        for (Map.Entry<Keyword, Double> entry : map.entrySet()) {
            sum += entry.getValue().doubleValue();
        }
        return sum;
    }
    //</editor-fold>

    //<editor-fold desc="IDF - Inverse document frequency">
    private void calculateInverseDocumentFrequency(Keywords keywords) {
        for (Keyword keyword : keywords) {
            calculateInverseDocumentFrequency(keyword);
        }
    }

    private void calculateInverseDocumentFrequency(Keyword keyword) {
        double numberOfDocuments = documents.size();
        double documentsContainingKeyword = calcNumberOfDocumentsContainingKeyword(keyword);
        double idf = 0;
        if (documentsContainingKeyword > 0) {
            idf = Math.log10(numberOfDocuments / documentsContainingKeyword);
        }
        IDF.put(keyword, idf);
    }


    private double calcNumberOfDocumentsContainingKeyword(Keyword keyword) {
        Map<Document, Double> map = termDocumentCount.get(keyword);
        if (map == null) {
            return 0;
        }

        int count = 0;
        for (Map.Entry<Document, Double> entry : map.entrySet()) {
            if (entry.getValue() > 0) {
                count++;
            }
        }
        return count;
    }
    //</editor-fold>

}
