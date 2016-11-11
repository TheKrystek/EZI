package pl.put.model;

/**
 * Author: Krystian Świdurski
 */
public class Query extends Document {
    public Query(String text) {
        super(null, text);
    }

    @Override
    public String toString() {
        return "QUERY: " + getText();
    }
}
