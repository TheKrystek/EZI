package pl.put.model;

import pl.put.services.FileKeywordsReader;
import pl.put.services.KeywordsReader;

import java.util.HashSet;

/**
 * Author: Rafał Sobkowiak
 */
public class StopWords extends HashSet<String> {

    KeywordsReader reader;

    public StopWords(String filename){
        reader  = new FileKeywordsReader(filename);
        try {
            Keywords stopWords =  reader.read();
            stopWords.forEach( (stopWord) -> this.add(stopWord.getValue()) );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
