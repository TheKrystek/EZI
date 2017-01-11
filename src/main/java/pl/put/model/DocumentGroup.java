package pl.put.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Rafał Sobkowiak
 */
public class DocumentGroup extends Document {

    @Setter
    @Getter
    Document initialCentroid;
    @Getter
    Integer groupNumber;
    @Getter
    List<Document> documents;
    @Setter
    @Getter
    Double scalarProduct;
    @Setter
    @Getter
    Double vectorLength;

    public void add(Document document){
        documents.add(document);
    }

    public DocumentGroup(Integer groupNumber){
        super();
        this.documents = new ArrayList<Document>();
        this.groupNumber = groupNumber;
    }
}
