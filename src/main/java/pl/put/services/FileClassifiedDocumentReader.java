package pl.put.services;

import lombok.Getter;
import pl.put.model.Document;
import pl.put.model.Documents;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Author: Rafał Sobkowiak
 */
public class FileClassifiedDocumentReader extends FileReader implements DocumentReader {

    public FileClassifiedDocumentReader(File file) {
        super(file);
    }

    public FileClassifiedDocumentReader(String file) {
        super(file);
    }

    @Getter
    HashSet<String> classNames;

    @Override
    public Documents read() throws Exception {
        classNames  = new HashSet<>();
        Documents documents = new Documents();
        checkIfFileExist();
        Scanner scanner = new Scanner(file);

        Document document = new Document();
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (isEmptyLine(line)) {
                document.setText(builder.toString());
                documents.add(document);
                builder = new StringBuilder();
                document = new Document();
            } else {
                if (document.getClassName() == null) {
                    document.setClassName(line);
                    classNames.add(line);
                } else if (document.getTitle() == null) {
                    document.setTitle(line);
                } else {
                    builder.append(line);
                }
            }
        }
        document.setText(builder.toString());
        documents.add(document);
        return documents;
    }


    private boolean isEmptyLine(String line) {
        return (line == null || line.trim().isEmpty());
    }
}
