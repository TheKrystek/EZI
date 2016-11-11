package pl.put.services;

import org.junit.Assert;
import org.junit.Test;
import pl.put.model.Document;
import pl.put.model.Keyword;

/**
 * Author: Krystian Świdurski
 */
public class StemmerTest {

    Stemmer stemmer = new Stemmer();
    Keyword keyword = new Keyword("AnaLysiS");
    Document document = new Document("Biologically Transparent", "Biologically transparent and accessible to interpretation");
    String string = "Such an analysis can reveal features that are not easily visible from the variations in individual genes";

    @Test
    public void run_Keyword() {
        stemmer.run(keyword);
        Assert.assertEquals("analysi", keyword.getStemmedValue());
    }

    @Test
    public void run_DocumentTitle() {
        stemmer.run(document);
        Assert.assertEquals("biolog transpar", document.getStemmedTitle());
    }

    @Test
    public void run_DocumentText() {
        stemmer.run(document);
        Assert.assertEquals("biolog transpar and access to interpret", document.getStemmedText());
    }

    @Test
    public void run_String() {
        Assert.assertEquals("such an analysi can reveal featur that ar not easili visibl from the variat in individu gene", stemmer.run(string));
    }
}