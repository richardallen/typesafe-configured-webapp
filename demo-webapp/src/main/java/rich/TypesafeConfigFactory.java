package rich;

import java.io.File;
import java.util.Optional;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Immediate;

/**
 * Supplies the Typesafe {@link Config} object for the application.
 */
@Immediate
@Slf4j
public class TypesafeConfigFactory implements Factory<Config> {
    private static final String OVERRIDES_CONFIG_FILE_KEY = "webapp.config.file";
    private final Config config;

    public TypesafeConfigFactory() {
        log.info("Creating instance of " + getClass().getName());
        log.info("Loading Typesafe configuration...");

        Config systemProperties = ConfigFactory.systemProperties();
        Config environmentVariables = ConfigFactory.systemEnvironment();
        Config externalOverrides = getExternalOverrides();
        Config internalOverrides = ConfigFactory.parseResources("overrides.conf");
        Config applicationConf = ConfigFactory.parseResources("application.conf");
        Config referenceConf = ConfigFactory.parseResources("reference.conf");

        /*  Load configuration with the following precedence.

            - Java System properties
            - Environment variables
            - External overrides file defined by the Java System property `webapp.config.file`,
              falling back to the file defined by the JNDI name `java:comp/env/webapp.config.file`.
            - "overrides.conf" files found on the classpath.
            - "application.conf" files found on the classpath.
            - "reference.conf" files found on the classpath.
         */
        config = systemProperties
                .withFallback(environmentVariables)
                .withFallback(externalOverrides)
                .withFallback(internalOverrides)
                .withFallback(applicationConf)
                .withFallback(referenceConf)
                .resolve(); // Resolve only after all the configuration is merged.
    }

    /**
     * Loads the overrides config file specified by the property "webapp.config.file".
     * Looks for the Java System property first, and if that's not found then the JNDI
     * name "java:comp/env/webapp.config.file". If no file is found then an empty
     * {@link Config} is returned.
     *
     * @return The overrides config or an empty config if no overrides file is found.
     */
    private Config getExternalOverrides() {
        if (Boolean.TRUE.equals(Boolean.valueOf(System.getProperty("no_external_override")))) {
            log.info("Skipping external override");
        } else {
            // In Java 9+ this is simply: getOverridesFromSystemProperty().or(this::getOverridesFromJndi)
            Optional<String> overridesPath =
                    getOverridesFromSystemProperty().map(Optional::of).orElseGet(this::getOverridesFromJndi);

            if (overridesPath.isPresent()) {
                File overridesFile = new File(overridesPath.get());

                if (overridesFile.exists()) {
                    log.info("Loading overrides config file " + overridesFile.getAbsolutePath());
                    return ConfigFactory.parseFile(overridesFile);
                } else {
                    log.error("The overrides config was not found at " + overridesFile.getAbsolutePath());
                }
            } else {
                log.info("No overrides config was named by the (System or JNDI) property " + OVERRIDES_CONFIG_FILE_KEY);
            }
        }

        return ConfigFactory.empty();
    }

    private Optional<String> getOverridesFromSystemProperty() {
        return Optional.ofNullable(System.getProperty(OVERRIDES_CONFIG_FILE_KEY));
    }

    private Optional<String> getOverridesFromJndi() {
        try {
            Context initCtx = new InitialContext();
            return Optional.ofNullable((String) initCtx.lookup("java:comp/env/" + OVERRIDES_CONFIG_FILE_KEY));
        } catch (Exception e) {
            log.warn("Failed to lookup JNDI property at java:comp/env/" + OVERRIDES_CONFIG_FILE_KEY, e);
            return Optional.empty();
        }
    }

    public Config getConfig() {
        return config;
    }

    /**
     * This HK2 {@link Factory} method makes the {@link Config} instance available for dependency injection.
     */
    @Override
    @Immediate
    public Config provide() {
        return getConfig();
    }

    /**
     * This method is required by HK2 {@link Factory} interface.
     */
    @Override
    public void dispose(Config instance) {
        // Nothing to do.
    }
}
