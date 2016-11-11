package pl.put.model;

import lombok.Getter;
import lombok.Setter;
import pl.put.services.Stemmer;

/**
 * Author: Krystian Świdurski
 */
public class Document implements Stemmable {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String text;

    @Getter
    private String stemmedTitle, stemmedText;

    public Document() {
    }

    public Document(String title, String text) {
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("TITLE: %s, TEXT: %s", title, text);
    }

    @Override
    public void stem(Stemmer stemmer) {
        stemmedTitle = stemmer.run(title);
        stemmedText = stemmer.run(text);
    }

    public String getContent() {
        return title + " " + text;
    }

    public String getStemmedContent() {
        return stemmedTitle + " " + stemmedText;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;

        if (title != null ? !title.equals(document.title) : document.title != null) return false;
        if (text != null ? !text.equals(document.text) : document.text != null) return false;
        if (stemmedTitle != null ? !stemmedTitle.equals(document.stemmedTitle) : document.stemmedTitle != null)
            return false;
        return stemmedText != null ? stemmedText.equals(document.stemmedText) : document.stemmedText == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (stemmedTitle != null ? stemmedTitle.hashCode() : 0);
        result = 31 * result + (stemmedText != null ? stemmedText.hashCode() : 0);
        return result;
    }
}
