package pl.put.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.put.services.Stemmer;
import pl.put.services.TFIDF;

/**
 * Author: Krystian Świdurski
 */
public class TFIDFTest {

    public static final double DELTA = 0.005;
    Stemmer stemmer = new Stemmer();
    Document d1, d2;
    Keyword _this, a, is, sample, another, example, outside;
    Keywords keywords;
    Documents documents;
    TFIDF tfidf;

    @Before
    public void setup() {
        d1 = new Document("A", "This is a sample");
        d2 = new Document("Example", "This is another example. Another example");
        documents = new Documents(d1, d2);
        _this = new Keyword("this");
        a = new Keyword("a");
        is = new Keyword("is");
        sample = new Keyword("sample");
        another = new Keyword("another");
        example = new Keyword("example");
        outside = new Keyword("example");
        keywords = new Keywords(_this, is, a, sample, another, example);
        stemmer.run(documents);
        stemmer.run(keywords);
        tfidf = new TFIDF(documents, keywords);
    }

    @Test
    public void getTFIDF() throws Exception {

    }

    @Test
    public void getTermFrequency_ThisInD1() throws Exception {
        Assert.assertEquals(0.2, tfidf.getTermFrequency(_this, d1), DELTA);
    }

    @Test
    public void getTermFrequency_ThisInD2() throws Exception {
        Assert.assertEquals(0.14, tfidf.getTermFrequency(_this, d2), DELTA);
    }


    @Test
    public void getTermFrequency_ExampleInD1() throws Exception {
        Assert.assertEquals(0.0, tfidf.getTermFrequency(example, d1), DELTA);
    }

    @Test
    public void getTermFrequency_ExampleInD2() throws Exception {
        Assert.assertEquals(0.429, tfidf.getTermFrequency(example, d2), DELTA);
    }

    @Test
    public void getTermFrequency_NotKnownTerm() throws Exception {
        Assert.assertEquals(0.0, tfidf.getTermFrequency(outside, d2), DELTA);
    }

    @Test
    public void getInverseDocumentFrequency_Example() throws Exception {
        Assert.assertEquals(0.301, tfidf.getInverseDocumentFrequency(example), DELTA);
    }

    @Test
    public void getInverseDocumentFrequency_Outside() throws Exception {
        Assert.assertEquals(0.0, tfidf.getInverseDocumentFrequency(outside), DELTA);
    }

    @Test
    public void getInverseDocumentFrequency_This() throws Exception {
        Assert.assertEquals(0.0, tfidf.getInverseDocumentFrequency(_this), DELTA);
    }


    @Test
    public void getTFIDF_ThisInD1() throws Exception {
        Assert.assertEquals(0.0, tfidf.getTFIDF(_this, d1), DELTA);
    }

    @Test
    public void getTFIDF_ThisInD2() throws Exception {
        Assert.assertEquals(0.0, tfidf.getTFIDF(_this, d2), DELTA);
    }

    @Test
    public void getTFIDF_ExampleInD1() throws Exception {
        Assert.assertEquals(0.0, tfidf.getTFIDF(example, d1), DELTA);
    }

    @Test
    public void getTFIDF_ExampleInD2() throws Exception {
        Assert.assertEquals(0.13, tfidf.getTFIDF(example, d2), DELTA);
    }
}
