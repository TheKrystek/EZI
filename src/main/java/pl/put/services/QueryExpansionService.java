package pl.put.services;

import lombok.Setter;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.dictionary.Dictionary;
import pl.put.Main;
import pl.put.model.*;

import java.lang.reflect.Array;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryExpansionService {

    @Setter
    private SimilarityModel similarity;

    @Setter
    private Stemmer stemmer;

    Dictionary dictionary;
    Double weightThreshold = 0.5;
    private StopWords stopWords = new StopWords(Main.DEFAULT_STOPWORDS_PATH);

    public List<QueryExpansion> expandQuery(SearchResults searchResults, Query query, Integer queryExpansionCount, Double weightThreshold) {
        this.weightThreshold = weightThreshold;
        List<SearchResult> results = searchResults.getResults().stream().filter(r -> r.getSimilarity() > 0).collect(Collectors.toList());

        //Ogranicz do 5 najlepszych wyników
        if (results.size() > 5) {
            results = results.subList(0, 5);
        }

        SearchResults relevantSearchResults = new SearchResults(query, results);


        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        //Pobierz synsety dla wszystkich części mowy
        HashSet<Synset> allSynsets = getQuerySynsets(dictionary, query);

        List<WordSenseDisambiguation> wsds = getWordSenseDisambiguations(allSynsets, relevantSearchResults, query);
        for (WordSenseDisambiguation wsd : wsds) {
            System.out.println(wsd.getSynset());
        }

        List<QueryExpansion> queryExpansion = getExpansionWords(wsds, query);

        if (queryExpansionCount != null && queryExpansionCount <= queryExpansion.size()) {
            return queryExpansion.subList(0, queryExpansionCount);
        } else {
            return queryExpansion;
        }

    }

    private HashSet<Synset> getQuerySynsets(Dictionary dictionary, Query query) {
        HashSet<Synset> synsets = new HashSet<Synset>();
        try {
            for (POS pos : POS.getAllPOS()) {
                IndexWord indexWord = dictionary.lookupIndexWord(pos, query.getText());

                if (indexWord != null) {
                    synsets.addAll(indexWord.getSenses());
                }

                //Znajdź synsety również dla osobnych fraz zapytania
                for (String word : query.getText().split(" ")) {
                    indexWord = dictionary.lookupIndexWord(pos, word);

                    if (indexWord != null) {
                        synsets.addAll(indexWord.getSenses());
                    }
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return synsets;
    }

    private List<WordSenseDisambiguation> getWordSenseDisambiguations(HashSet<Synset> synsets, SearchResults relevantSearchResults, Query query) {
        List<WordSenseDisambiguation> wsds = new ArrayList<WordSenseDisambiguation>();

        for (Synset synset : synsets) {
            Double weight = 0.0;
            HashSet<String> synsetWords = new HashSet<String>();

            PointerTargetNodeList hypernyms = null;

            try {
                hypernyms = PointerUtils.getDirectHypernyms(synset);
            } catch (JWNLException e) {
                e.printStackTrace();
            }

            //Dodaj hiperonimy
            if (hypernyms != null) {
                for (PointerTargetNode node : hypernyms) {
                    for (Word word : node.getSynset().getWords()) {
                        for (String lemma : word.getLemma().split(" ")) {
                            if (!query.getText().contains(lemma)) {
                                synsetWords.add(stemmer.stemmerEngine.stem(lemma));
                            }
                        }
                    }
                }
            }

            //Dodaj synonimy niewystępujące w zapytaniu
            for (Word word : synset.getWords()) {
                if (!query.getText().contains(word.getLemma())) {
                    synsetWords.add(stemmer.stemmerEngine.stem(word.getLemma()));
                }
            }


            //Dodaj słowa występujące w definicji
            String[] test = synset.getGloss().replaceAll("[.:;,()]", "").split(" ");
            for (String glossWord : synset.getGloss().replaceAll("[.:;,()]", "").split(" ")) {
                if (!query.getText().contains(glossWord)) {
                    String stemmed = stemmer.stemmerEngine.stem(glossWord);
                    synsetWords.add(stemmed);
                }
            }

            //Usuń stop words
            synsetWords.removeAll((stopWords));
            for (SearchResult searchResult : relevantSearchResults.getResults()) {
                HashSet<String> documentWords = new HashSet<String>();
                documentWords.addAll(Arrays.asList(searchResult.getDocument().getStemmedContent().split(" ")));
                documentWords.retainAll(synsetWords);
                weight += (documentWords.size()) * searchResult.getSimilarity();
            }

            if (weight > 0) {
                WordSenseDisambiguation wsd = new WordSenseDisambiguation(synset);
                wsd.setWeight(weight);
                wsds.add(wsd);
            }
        }

        if (wsds.isEmpty() && !synsets.isEmpty()) {
            wsds.add(new WordSenseDisambiguation(synsets.stream()
                    .max(Comparator.comparing(synset -> synset.getPointers().size()))
                    .get()));
        }

        System.out.println();
        wsds = wsds.parallelStream().sorted((wsd1, wsd2) -> -1 * Double.compare(wsd1.getWeight(), wsd2.getWeight())).collect(Collectors.toList());
        normalizeWsdWeights(wsds);
        wsds = wsds.parallelStream().filter(w -> w.getWeight() >= weightThreshold).collect(Collectors.toList());
        return wsds;
    }

    private List<QueryExpansion> getExpansionWords(List<WordSenseDisambiguation> wsds, Query query) {
        List<QueryExpansion> queryExpansions = new ArrayList<QueryExpansion>();

        for (WordSenseDisambiguation wsd : wsds) {
            if (wsd.getWeight() < weightThreshold) {
                continue;
            }
            PointerTargetNodeList hypernyms = null;
            PointerTargetNodeList hyponyms = null;

            try {
                hypernyms = PointerUtils.getDirectHypernyms(wsd.getSynset());
                hyponyms = PointerUtils.getDirectHyponyms(wsd.getSynset());
            } catch (JWNLException e) {
                e.printStackTrace();
            }

            if (hypernyms != null) {
                for (PointerTargetNode node : hypernyms) {
                    for (Word word : node.getSynset().getWords()) {
                        if (!query.getText().toLowerCase().contains(word.getLemma())) {
                            QueryExpansion queryExpansion = new QueryExpansion(word.getLemma());
                            queryExpansion.setPointerCount(word.getSynset().getPointers().size());
                            queryExpansion.setWeight(wsd.getWeight());
                            queryExpansions.add(queryExpansion);
                        }
                    }
                }
            }

            if (hyponyms != null) {
                for (PointerTargetNode node : hyponyms) {
                    for (Word word : node.getSynset().getWords()) {
                        if (!query.getText().toLowerCase().contains(word.getLemma())) {
                            QueryExpansion queryExpansion = new QueryExpansion(word.getLemma());
                            queryExpansion.setPointerCount(word.getSynset().getPointers().size());
                            queryExpansion.setWeight(wsd.getWeight());
                            queryExpansions.add(queryExpansion);
                        }
                    }
                }
            }

            for (Word word : wsd.getSynset().getWords()) {
                if (!query.getText().toLowerCase().contains(word.getLemma())) {
                    QueryExpansion queryExpansion = new QueryExpansion(word.getLemma());
                    queryExpansion.setPointerCount(word.getSynset().getPointers().size());
                    queryExpansion.setWeight(wsd.getWeight());
                    queryExpansions.add(queryExpansion);
                }
            }
        }
        normalizeQueryExpansionWeights(queryExpansions);
        queryExpansions = queryExpansions.parallelStream().sorted((q1, q2) -> -1 * Double.compare(q1.getWeight(), q2.getWeight())).collect(Collectors.toList());
        queryExpansions = queryExpansions.parallelStream().filter(q -> q.getWeight() >= weightThreshold).collect(Collectors.toList());
        return queryExpansions;
    }

    private void normalizeWsdWeights(List<WordSenseDisambiguation> wsds) {
        Double max = wsds.get(0).getWeight();
        Double min = 0.0;

        for (WordSenseDisambiguation wsd : wsds) {
            if (wsd.getWeight() == 0) {
                continue;
            }
            Double normalized = (1 - 0) / (max - min) * (wsd.getWeight() - min) + 0;
            wsd.setWeight(normalized);
        }
    }

    private void normalizeQueryExpansionWeights(List<QueryExpansion> queryExpansions) {
        Double maxw = queryExpansions.stream().max(Comparator.comparing(i -> i.getWeight())).get().getWeight();
        Integer maxp = queryExpansions.stream().max(Comparator.comparing(i -> i.getPointerCount())).get().getPointerCount();
        Double min = 0.0;

        for (QueryExpansion queryExpansion : queryExpansions) {
            if (queryExpansion.getWeight() == 0) {
                continue;
            }
            Double normalizedw = (1 - 0) / (maxw - min) * (queryExpansion.getWeight() - min) + 0;
            Double normalizedp = (1 - 0) / (maxp - min) * (queryExpansion.getPointerCount() - min) + 0;
            queryExpansion.setWeight(normalizedw + normalizedp);
        }

        maxw = queryExpansions.stream().max(Comparator.comparing(i -> i.getWeight())).get().getWeight();

        for (QueryExpansion queryExpansion : queryExpansions) {
            if (queryExpansion.getWeight() == 0) {
                continue;
            }
            Double normalizedw = (1 - 0) / (maxw - min) * (queryExpansion.getWeight() - min) + 0;
            queryExpansion.setWeight(normalizedw);
        }
    }
}
