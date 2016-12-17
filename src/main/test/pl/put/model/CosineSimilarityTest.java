package pl.put.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.put.services.Stemmer;
import pl.put.services.TFIDF;

/**
 * Author: Krystian Świdurski
 */
public class CosineSimilarityTest {

    public static final double DELTA = 0.0005;
    private static Stemmer stemmer = new Stemmer();
    private TFIDF tfidf;
    private Document d1 = new Document("information retrieval information retrieval");
    private Document d2 = new Document("retrieval retrieval retrieval retrieval");
    private Document d3 = new Document("agency information retrieval agency");
    private Document d4 = new Document("retrieval agency retrieval agency");
    private Query q = new Query("information retrieval");
    private Keywords keywords = new Keywords("information", "retrieval", "agency");
    private Documents documents;


    @Before
    public void setup() {
        documents = new Documents(d1, d2, d3, d4);
        stemmer.run(documents);
        stemmer.run(keywords);
        stemmer.run(q);
        tfidf = new TFIDF(documents, keywords);
    }

    @Test
    public void calculate_D1AndQuery() throws Exception {
        CosineSimilarity similarity = new CosineSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(1.0, similarity.calculate(q, d1), DELTA);
    }

    @Test
    public void calculate_D2AndQuery() throws Exception {
        CosineSimilarity similarity = new CosineSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(0.0, similarity.calculate(q, d2), DELTA);
    }

    @Test
    public void calculate_D3AndQuery() throws Exception {
        CosineSimilarity similarity = new CosineSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(0.447, similarity.calculate(q, d3), DELTA);
    }

    @Test
    public void calculate_D4AndQuery() throws Exception {
        CosineSimilarity similarity = new CosineSimilarity();
        similarity.setTFIDF(tfidf);
        Assert.assertEquals(0.0, similarity.calculate(q, d4), DELTA);
    }


}