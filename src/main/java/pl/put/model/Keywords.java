package pl.put.model;

import java.util.ArrayList;

/**
 * Author: Krystian Świdurski
 */
public class Keywords extends ArrayList<Keyword> {

    public Keywords(){

    }

    public Keywords(String... keywords){
        for (String keyword : keywords) {
            add(new Keyword(keyword));
        }
    }

    public Keywords(Keyword... keywords){
        setAll(keywords);
    }

    public void setAll(Keyword... keywords) {
        clear();
        for (Keyword keyword : keywords) {
            add(keyword);
        }
    }
}
