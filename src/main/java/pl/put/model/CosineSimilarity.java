package pl.put.model;

import pl.put.services.TFIDF;

import java.util.Map;

/**
 * Author: Krystian Świdurski
 */
public class CosineSimilarity implements SimilarityModel {
    private TFIDF TFIDF;
    private Map<Keyword, Double> queryTFIDF;

    @Override
    public void setTFIDF(TFIDF tfidf) {
        this.TFIDF = tfidf;
    }

    @Override
    public double calculate(Document d1, Document d2) {
        double vectorProduct = getVectorProduct(d1, d2);
        if (vectorProduct == 0) {
            return 0;
        }
        return getScalarProduct(d1, d2) / vectorProduct;
    }

    @Override
    public double calculate(Query query, Document document) {
        queryTFIDF = TFIDF.getTFIDF(query);
        double vectorProduct = getVectorProduct(document);
        if (vectorProduct == 0) {
            return 0;
        }
        return getScalarProduct(document) / vectorProduct;
    }

    public double calculate(Document d, DocumentGroup g) {
        double vectorProduct = getVectorProduct(d, g);
        if (vectorProduct == 0) {
            return 0;
        }
        return getScalarProduct(d, g) / vectorProduct;
    }

    private double getScalarProduct(Document d1, Document d2) {
        double product = 0;
        for (Keyword keyword : TFIDF.getKeywords()) {
            product += TFIDF.getTFIDF(keyword, d1) * TFIDF.getTFIDF(keyword, d2);
        }
        return product;
    }

    private double getScalarProduct(Document d1, DocumentGroup g) {
        double product = 0;
        Integer documentCount = g.getDocuments().size();
            for (Keyword keyword : TFIDF.getKeywords()) {

                double gTFIDF = 0.0;
                //Zsumuj TFIDF słowa kluczowego dla wszystkich dokumnetów w grupie
                for(Document document : g.getDocuments()) {
                    gTFIDF+=TFIDF.getTFIDF(keyword, document);
                }
                //Oblicz średnią
                gTFIDF/=documentCount;
                product += TFIDF.getTFIDF(keyword, d1) *  gTFIDF;
            }
        return product;
    }

    private double getScalarProduct(Document document) {
        double product = 0;
        for (Keyword keyword : TFIDF.getKeywords()) {
            product += TFIDF.getTFIDF(keyword, document) * queryTFIDF.getOrDefault(keyword, 0.0);
        }
        return product;
    }

    //<editor-fold desc="Vector product">
    private double getVectorProduct(Document d1, Document d2) {
        return getVectorLength(d1) * getVectorLength(d2);
    }

    private double getVectorProduct(Document d, DocumentGroup g) {
        Double vectorLength = g.getVectorLength();

        if(vectorLength == null){
            g.setVectorLength(getVectorLength(g));
            vectorLength = g.getVectorLength();
        }

        return getVectorLength(d) * vectorLength;
    }

    public double getVectorProduct(Document document) {
        return getVectorLength(document) * getQueryVectorLength();
    }

    private double getVectorLength(Document document) {
        double sumOfSquares = 0;
        for (Keyword keyword : TFIDF.getKeywords()) {
            double value = TFIDF.getTFIDF(keyword, document);
            sumOfSquares += (value * value);
        }
        return Math.sqrt(sumOfSquares);
    }

    private double getVectorLength(DocumentGroup documentGroup) {
        double sumOfSquares = 0;
        Integer documentCount = documentGroup.getDocuments().size();

        for (Keyword keyword : TFIDF.getKeywords()) {
            double value = 0;
            //Zsumuj TFIDF słowa kluczowego dla wszystkich dokumnetów w grupie
            for(Document document : documentGroup.getDocuments()) {
                value+= TFIDF.getTFIDF(keyword, document);
            }
            //Oblicz średnią
            value /= documentCount;
            sumOfSquares += (value * value);
        }
        return Math.sqrt(sumOfSquares);
    }

    private double getQueryVectorLength() {
        double sumOfSquares = 0;
        for (Keyword keyword : TFIDF.getKeywords()) {
            double value = queryTFIDF.getOrDefault(keyword, 0.0);
            sumOfSquares += (value * value);
        }
        return Math.sqrt(sumOfSquares);
    }
    //</editor-fold>
}
