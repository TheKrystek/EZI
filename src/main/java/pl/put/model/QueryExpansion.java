package pl.put.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: Rafał Sobkowiak
 */
public class QueryExpansion extends Query{

    @Getter
    @Setter
    Double weight = 0.0;
    @Getter
    @Setter
    Integer pointerCount = 0;

    public QueryExpansion(String text) {
        super(text);
    }


}
