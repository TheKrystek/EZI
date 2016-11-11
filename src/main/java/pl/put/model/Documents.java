package pl.put.model;

import java.util.ArrayList;

/**
 * Author: Krystian Świdurski
 */
public class Documents extends ArrayList<Document> {

    public Documents(Document... documents) {
        setAll(documents);
    }

    public void setAll(Document... documents) {
        clear();
        for (Document document : documents) {
            add(document);
        }
    }
}
