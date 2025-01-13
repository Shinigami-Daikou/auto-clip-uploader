package org.shinigami.config;

import io.smallrye.config.WithConverter;

public interface Clip {
    String name();

    @WithConverter(TimeConverter.class)
    Long start();

    @WithConverter(TimeConverter.class)
    Long end();
}
