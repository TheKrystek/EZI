package pl.put.model;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Krystian Świdurski
 */
public class SearchResults {

    public SearchResults(Query query, List<SearchResult> results) {
        this.query = query;
        this.results = results;
        order();
    }

    private void order() {
        results = results.parallelStream().sorted((o1, o2) -> -1 * Double.compare(o1.getSimilarity(), o2.getSimilarity())).collect(Collectors.toList());
    }

    @Getter
    private Query query;

    @Getter
    private List<SearchResult> results;


    public List<SearchResult> getResults(boolean showAll) {
        if (showAll) {
            return getResults();
        }
        return results.parallelStream().filter(p -> p.getSimilarity() > 0).collect(Collectors.toList());
    }
}
