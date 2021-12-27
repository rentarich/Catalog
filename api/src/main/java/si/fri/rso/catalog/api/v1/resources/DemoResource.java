package si.fri.rso.catalog.api.v1.resources;


import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import si.fri.rso.catalog.services.config.RestProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;

@Log
@ApplicationScoped
@Path("/demo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DemoResource {
    private com.kumuluz.ee.logs.Logger logger = LogManager.getLogger(DemoResource.class.getName());

    private Logger log = Logger.getLogger(DemoResource.class.getName());

    @Inject
    private RestProperties restProperties;

    @POST
    @Operation(description = "Intentionally break MS.", summary = "Break MS",
            tags = "demo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "MS broken"),
            })
    @Path("break")
    public Response makeUnhealthy() {
        logger.warn("Making service unhealhty, it should get killed by K8S soon.");
        restProperties.setMaintenance(true);

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Operation(description = "Change other's MS link to demonstrate FTP.", summary = "Break MS",
            tags = "demo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Link changed broken"),
            })
    @Path("url/{url}")
    public Response changeUrl(@PathParam("url")String url) {
        log.info(url);
        restProperties.setUrl(url);
        logger.warn("Registering MS reccomendation on url: "+url+" to test Fault tolerance patterns");
        log.info(String.valueOf(ConfigurationUtil.getInstance().get("rest-properties.url")));
        return Response.status(Response.Status.OK).build();
    }
}