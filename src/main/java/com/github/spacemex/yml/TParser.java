package com.github.spacemex.yml;

/**
 * Functional interface for parsing raw objects into a specific type.
 *
 * @param <T> the target type produced by the parser
 */
@FunctionalInterface
public interface TParser<T> {

    /**
     * Parses a raw object into the target type.
     *
     * @param raw the raw input object to parse
     * @return the parsed object of type T
     * @throws Exception if parsing fails
     */
    T parse(Object raw) throws Exception;
}
