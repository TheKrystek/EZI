package pl.put.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.put.services.Stemmer;
import pl.put.services.TFIDF;

/**
 * Author: Krystian Świdurski
 */
public class JaccardSimilarityTest {

    public static final double DELTA = 0.0005;
    private static Stemmer stemmer = new Stemmer();
    private TFIDF tfidf;
    private Document d1 = new Document("information retrieval information retrieval");
    private Document d2 = new Document("retrieval retrieval retrieval retrieval");
    private Document d3 = new Document("agency information retrieval agency");
    private Document d4 = new Document("retrieval agency retrieval agency");
    private Keywords keywords = new Keywords("information", "retrieval", "agency");
    private Documents documents;


    @Before
    public void setup() {
        documents = new Documents(d1, d2, d3, d4);
        stemmer.run(keywords);
        stemmer.run(documents);
        tfidf = new TFIDF(documents, keywords);
    }


    @Test
    public void calculate_D1AndD1() throws Exception {
        SimilarityModel similarity = new JaccardSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(1.0, similarity.calculate(d1, d1), DELTA);
    }

    @Test
    public void calculate_D1AndD2() throws Exception {
        SimilarityModel similarity = new JaccardSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(0.5, similarity.calculate(d1, d2), DELTA);
    }

    @Test
    public void calculate_D1AndD3() throws Exception {
        SimilarityModel similarity = new JaccardSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(2.0 / 3.0, similarity.calculate(d1, d3), DELTA);
    }

    @Test
    public void calculate_D1AndD4() throws Exception {
        SimilarityModel similarity = new JaccardSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(1.0 / 3.0, similarity.calculate(d1, d4), DELTA);
    }

}