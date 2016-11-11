package pl.put.services;

import pl.put.model.Keyword;
import pl.put.model.Keywords;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Krystian Świdurski
 */
public class FileKeywordsReader extends FileReader implements KeywordsReader{

    public FileKeywordsReader(File file){
        super(file);
    }

    public FileKeywordsReader(String file){
        super(file);
    }

    @Override
    public Keywords read() throws Exception {
        Keywords keywords = new Keywords();
        checkIfFileExist();

        List<String> lines = Files.readAllLines(file.toPath());
        keywords.addAll(lines.stream().map(Keyword::new).collect(Collectors.toList()));
        return keywords;
    }
}
