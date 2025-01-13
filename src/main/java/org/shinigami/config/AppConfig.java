package org.shinigami.config;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@StaticInitSafe
@ConfigMapping(prefix = "auto-clip-uploader", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface AppConfig {
    Video video();
    Youtube youtube();
}