package pl.put.model;

import lombok.Getter;
import pl.put.services.Stemmer;

/**
 * Author: Krystian Świdurski
 */
public class Keyword implements Stemmable {
    @Getter
    private String value, stemmedValue;

    public Keyword(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void stem(Stemmer stemmer) {
        stemmedValue = stemmer.run(value);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keyword keyword = (Keyword) o;

        if (value != null ? !value.equals(keyword.value) : keyword.value != null) return false;
        return stemmedValue != null ? stemmedValue.equals(keyword.stemmedValue) : keyword.stemmedValue == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (stemmedValue != null ? stemmedValue.hashCode() : 0);
        return result;
    }
}
