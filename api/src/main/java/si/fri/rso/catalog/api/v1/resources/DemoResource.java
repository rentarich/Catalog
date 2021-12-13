package si.fri.rso.catalog.api.v1.resources;


import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import si.fri.rso.catalog.services.config.RestProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/demo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DemoResource {

    private Logger log = Logger.getLogger(DemoResource.class.getName());

    @Inject
    private RestProperties restProperties;

    @POST
    @Path("break")
    public Response makeUnhealthy() {

        restProperties.setMaintenance(true);

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("url/{url}")
    public Response changeUrl(@PathParam("url")String url) {
        log.info(url);
        restProperties.setUrl(url);
        log.info(String.valueOf(ConfigurationUtil.getInstance().get("rest-properties.url")));
        return Response.status(Response.Status.OK).build();
    }
}