package org.shinigami.config;

import org.eclipse.microprofile.config.spi.Converter;

import java.time.Duration;

public class TimeConverter implements Converter<Long> {
    @Override
    public Long convert(final String configTime) throws IllegalArgumentException, NullPointerException {
        String time = configTime;
        String formattedTime = "PT" + time.replaceFirst(":", "H").replace(":", "M") + "S";
        return (long)Duration.parse(formattedTime).getSeconds() * 1000 * 1000;
    }
}
