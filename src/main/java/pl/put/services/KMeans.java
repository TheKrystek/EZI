package pl.put.services;

import lombok.Getter;
import pl.put.model.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Author: Rafał Sobkowiak
 */
public class KMeans {

    @Getter
    private Documents documents;
    @Getter
    private final Integer iterations;
    @Getter
    private final Integer k;

    HashMap<Integer,DocumentGroup> documentGroups;
    CosineSimilarity cosineSimilarity;

    public KMeans(Documents documents, Integer k, TFIDF tfidf, Integer iterations) {
        this.documents = documents;
        this.k = k;
        this.cosineSimilarity = new CosineSimilarity();
        this.cosineSimilarity.setTFIDF(tfidf);
        this.iterations = iterations;
        documentGroups = new HashMap<Integer,DocumentGroup>();
        run();
    }


    private void selectRandomDocuments(){
        SecureRandom random = new SecureRandom();

        for(Integer i =0; i < k ;i++){
            DocumentGroup documentGroup = new DocumentGroup(i);
            documentGroups.put(i,documentGroup);

            Document randomDocument = this.documents.get(random.nextInt(this.documents.size()));
            this.documents.remove(randomDocument);
            randomDocument.setGroupNumber(i);
            documentGroup.add(randomDocument);
            documentGroup.setInitialCentroid(randomDocument);
        }
    }

    public void run() {
        initialize();
        for(Integer i = 0; i< iterations;i++){
            if(calculateAndAssignNewGroups()){
                System.out.println("Stopped after " + (i+1) + " iterations");
                break;
            }
        }

        documents = documents.parallelStream().sorted((d1, d2) -> Integer.compare(d1.getGroupNumber(), d2.getGroupNumber())).collect(Collectors.toCollection(Documents::new));
    }

    private boolean calculateAndAssignNewGroups(){

        HashMap<Integer,DocumentGroup> newDocumentGroups = new HashMap<Integer,DocumentGroup>();
        boolean stopCondition = true;

        for(int i =0;i<k;i++){
            newDocumentGroups.put(i,new DocumentGroup(i));
        }

        for(Document document : documents){
            DocumentGroup documentGroup = getDocumentGroup(document);
            //Jeżeli dokument zmienił grupę, zmień warunek stopu
            if(!documentGroup.getGroupNumber().equals(document.getGroupNumber())){
                stopCondition = false;
                document.setGroupNumber(documentGroup.getGroupNumber());
            }

            newDocumentGroups.get(documentGroup.getGroupNumber()).add(document);
        }

        documentGroups = newDocumentGroups;
        return stopCondition;
    }

    public void initialize() {
        selectRandomDocuments();

        //Przypisz dokumenty do początkowych grup losowych
        for(Document document : this.documents){
            DocumentGroup documentGroup = getDocumentGroup(document);
            document.setGroupNumber(documentGroup.getGroupNumber());
            documentGroup.add(document);
        }

        for(Integer groupNumber: documentGroups.keySet()){
            this.documents.add(documentGroups.get(groupNumber).getInitialCentroid());
        }
    }

    private DocumentGroup getDocumentGroup(Document document){
        DocumentGroup retGroup = documentGroups.get(0);
        Double maxSimilarity = 0.0;
        Double similarity;

        for(Integer groupNumber: documentGroups.keySet()){
            DocumentGroup documentGroup = documentGroups.get(groupNumber);
            //Oblicz podobieństwo dokumentu do grupy
            similarity = cosineSimilarity.calculate(document,documentGroup);
            if(similarity > maxSimilarity){
                maxSimilarity = similarity;
                retGroup = documentGroup;
            }
        }

        return retGroup;
    }
}
