package pl.put.services;

import pl.put.model.Documents;

/**
 * Author: Krystian Świdurski
 */
public interface DocumentReader {
    Documents read() throws Exception;
}
