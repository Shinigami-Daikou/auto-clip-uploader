package org.shinigami.config;

import io.smallrye.config.WithConverter;

import java.util.List;
import java.util.Optional;

public interface Youtube {
    Optional<String> originalName();
    Optional<String> categoryId();
    @WithConverter(TagsConverter.class)
    Optional<List<String>> tags();
    Optional<String> videoStatus();
}
