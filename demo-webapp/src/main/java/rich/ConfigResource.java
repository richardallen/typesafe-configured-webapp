package rich;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;

/**
 * A simple REST endpoint that returns the Typesafe Configuration as JSON without the "server" path.
 */
@Path("/config")
public class ConfigResource {
    private final Config config;

    @Inject
    public ConfigResource(Config config) {
        this.config = config;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getConfig() {
        return config
                .getConfig("rich")
                .withoutPath("server") // Remove config that shouldn't be sent to the client.
                .root()
                .render(ConfigRenderOptions.concise());
    }
}
