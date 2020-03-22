package web.rest.boundary;

/*
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import web.boundary.TwitterAnalyzerApi;
*/


import org.glassfish.jersey.server.ResourceConfig;
import web.boundary.TwitterAnalyzerApi;

import javax.ws.rs.ApplicationPath;

/**
 * JAX RS application class.
 *
 * @author jog
 */
@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    /**
     * The default constructor.
     */
    public ApplicationConfig() {
        super();

        // Disable Moxy, we use Jackson
        //property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true);

        // register own provider classes
        //register(RestExceptionMapper.class);

        // register own resource classes
        register(TwitterAnalyzerApi.class);

        // register Jackson provider
        // workaround for GF4.1 bug
        // for details: https://java.net/jira/browse/GLASSFISH-21141
        /*final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JaxbAnnotationModule());
        register(new JacksonJaxbJsonProvider(mapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));*/
    }

}
