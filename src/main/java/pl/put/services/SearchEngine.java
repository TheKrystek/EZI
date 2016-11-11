package pl.put.services;

import lombok.Getter;
import pl.put.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Krystian Świdurski
 */
public class SearchEngine {

    @Getter
    private final Stemmer stemmer;
    @Getter
    private final TFIDF tfidf;
    @Getter
    private SimilarityModel similarity;

    private TFIDF queryTFIDF;

    public SearchEngine(Stemmer stemmer, TFIDF tfidf) {
        this.stemmer = stemmer;
        this.tfidf = tfidf;
    }

    public SearchResults search(Query query) {
        // Stem query
        query.stem(stemmer);

        // Create separate TF-IDF for query cause we don't want to add query to the documents
        queryTFIDF = new TFIDF(query, tfidf.getKeywords());

        // Create list of search results
        List<SearchResult> searchResults = tfidf.getDocuments().parallelStream().map(document -> compare(query, document)).collect(Collectors.toList());
        return new SearchResults(query, searchResults);

    }


    private SearchResult compare(Query query, Document document) {
        double sim = 0;
        if (similarity != null) {
            sim = similarity.calculate(query, document);
        }
        return new SearchResult(document, sim);
    }

    public void setSimilarity(SimilarityModel similarity) {
        this.similarity = similarity;
        this.similarity.setTFIDF(tfidf);
    }
}
