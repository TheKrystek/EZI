package pl.put.services;

import pl.put.model.Document;
import pl.put.model.Documents;

import java.io.File;
import java.util.Scanner;

/**
 * Author: Krystian Świdurski
 */
public class FileDocumentReader extends FileReader implements DocumentReader {

    public FileDocumentReader(File file) {
        super(file);
    }

    public FileDocumentReader(String file) {
        super(file);
    }

    @Override
    public Documents read() throws Exception {
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
                if (document.getTitle() == null) {
                    document.setTitle(line);
                } else {
                    builder.append(line);
                }
            }
        }
        return documents;
    }


    private boolean isEmptyLine(String line) {
        return (line == null || line.trim().isEmpty());
    }
}
