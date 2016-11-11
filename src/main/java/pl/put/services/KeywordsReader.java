package pl.put.services;

import pl.put.model.Keywords;

/**
 * Author: Krystian Świdurski
 */
public interface KeywordsReader  {
    Keywords read() throws Exception;
}
