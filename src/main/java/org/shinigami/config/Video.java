package org.shinigami.config;

import io.smallrye.config.WithConverter;

import java.util.List;
import java.util.Optional;

public interface Video {
    String videoPath();

    @WithConverter(TimeConverter.class)
    Optional<Long> delay();

    List<Clip> clips();
}
