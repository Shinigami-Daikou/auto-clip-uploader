package org.shinigami;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@QuarkusMain
public class QuarkusApp {

    public static void main(String... args) {
        Quarkus.run(ClipApplication.class, (integer, throwable) -> {
            Log.info("Error in ClipApplication: ", throwable);
        }, args);
    }
}
