package rich;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.typesafe.config.Config;

/**
 * A simple REST endpoint that returns a plain text welcome message from Typesafe configuration.
 */
@Singleton
@Path("/welcome")
public class WelcomeResource {
    private final Config config;

    @Inject
    public WelcomeResource(Config config) {
        this.config = config;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getWelcome() {
        return config.getString("rich.server.welcome");
    }
}
