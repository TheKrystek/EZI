package pl.put.model;

import lombok.Getter;

/**
 * Author: Krystian Świdurski
 */
public class SearchResult {
    @Getter
    private Document document;

    @Getter
    private Double similarity;

    public SearchResult(Document document, Double similarity) {
        this.document = document;
        this.similarity = similarity;
    }
}
