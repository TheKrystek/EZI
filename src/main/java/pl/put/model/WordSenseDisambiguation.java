package pl.put.model;

import lombok.Getter;
import lombok.Setter;
import net.sf.extjwnl.data.Synset;

import java.util.HashSet;

/**
 * Author: Rafał Sobkowiak
 */
public class WordSenseDisambiguation{

    @Getter
    @Setter
    Double weight = 0.0;
    @Getter
    @Setter
    Synset synset;

    public WordSenseDisambiguation(Synset synset){
        this.synset = synset;
    }

}
