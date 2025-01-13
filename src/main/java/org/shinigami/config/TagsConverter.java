package org.shinigami.config;

import org.eclipse.microprofile.config.spi.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagsConverter implements Converter<List<String>> {
    @Override
    public List<String> convert(final String s) throws IllegalArgumentException, NullPointerException {
        if(s == null || s.isEmpty())
            return null;
        List<String> tags = Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList());
        return tags;
    }
}
