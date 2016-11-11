package pl.put.services;

import lombok.Getter;

import java.io.File;

/**
 * Author: Krystian Świdurski
 */
public abstract class FileReader {

    @Getter
    protected File file;

    public FileReader(File file) {
        this.file = file;
    }

    public FileReader(String file) {
        this(new File(file));
    }

    protected void checkIfFileExist() throws Exception {
        if (file == null) {
            throw new NullPointerException("File cannot be null");
        }
        if (!file.exists()) {
            throw new Exception(file.getPath() + " file doesn't exist");
        }
    }
}
