package pl.put.services;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;
import pl.put.model.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryExpansion {

    public List<Query> expandQuery(Query query) {

        Dictionary dictionary = null;

        try {
            dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        List<Query> queryExpansion = new ArrayList<Query>();
        List<Synset> allSynsets = getPhraseSynsets(dictionary,query);
        List<Word> allWords = new ArrayList<Word>();


        List<String> allPhrases = Arrays.asList(query.getText().split(" "));

        //Pobierz wszystkie synsety i słowa dla całej frazy
        allSynsets = allSynsets.parallelStream().sorted((s1, s2) -> -1 * Integer.compare(s1.getPointers().size(), s2.getPointers().size())).collect(Collectors.toList());

        for (Synset synset : allSynsets) {
            for (Word word : synset.getWords()) {
                if (!word.getLemma().equals(query.getText()) && !queryExpansion.contains(new Query(word.getLemma()))) {
                    queryExpansion.add(new Query(word.getLemma()));
                }
            }
        }

        return queryExpansion;
    }

    private List<Synset> getPhraseSynsets(Dictionary dictionary, Query query){
        List<Synset> synsets = new ArrayList<Synset>();
        try {
            //Pobierz synsety dla wszystkich części mowy
            for (POS pos : POS.getAllPOS()) {
                IndexWord indexWord = dictionary.lookupIndexWord(pos, query.getText());
                if (indexWord != null) {
                    synsets.addAll(indexWord.getSenses());
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return synsets;
    }
}
