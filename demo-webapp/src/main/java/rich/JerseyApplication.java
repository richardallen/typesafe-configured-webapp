package rich;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey configuration.
 */
@ApplicationPath("/api")
public class JerseyApplication extends ResourceConfig {
    @Inject
    public JerseyApplication(ServiceLocator serviceLocator) {
        // Enable `@Immediate` so that HK2 will create the `TypesafeConfigFactory` immediately instead of lazily.
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
        ServiceLocatorUtilities.addClasses(serviceLocator, TypesafeConfigFactory.class);

        // Scan the package for the other Jersey resources to register.
        packages(true, this.getClass().getPackage().getName());
    }
}
