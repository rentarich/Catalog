package si.fri.rso.catalog.api.v1.resources;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.slf4j.ILoggerFactory;
import si.fri.rso.catalog.models.dtos.Item;
import si.fri.rso.catalog.services.beans.ItemBean;
import si.fri.rso.catalog.services.config.RestProperties;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemsResource {
    private Logger log = Logger.getLogger(ItemsResource.class.getName());
    private Client httpClient;
    private String baseUrl;

    @Inject
    private ItemBean itemBean;

    @Inject
    private RestProperties restProperties;

    @Context
    protected UriInfo uriInfo;


    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = ConfigurationUtil.getInstance().get("kumuluzee.server.base-url").orElse("N/A");
    }

    @GET
    public Response getItems() {

        log.info(System.getenv().get("config_2_value"));
        log.info(restProperties.getMaintenance());

        List<Item> imageMetadata = itemBean.getItemsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(imageMetadata).build();
    }

    @GET
    @Path("category/{category}")
    public Response getItems(@PathParam("category") String category) {

        List<Item> imageMetadata = itemBean.getItemsFilterCategory(uriInfo, category);

        return Response.status(Response.Status.OK).entity(imageMetadata).build();
    }

    @GET
    @Path("/{itemId}")
    public Response getItem(@PathParam("itemId") Integer itemId) {

        Item item = itemBean.getItem(itemId);

        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(item).build();
    }

    @POST
    public Response createItem(Item item) {


        if ((item.getTitle() == null || item.getDescription() == null || item.getCategory() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            item = itemBean.createItem(item);
        }

        return Response.status(Response.Status.CONFLICT).entity(item).build();

    }

    @PUT
    @Path("{itemId}")
    public Response putItem(@PathParam("itemId") Integer itemId,
                            Item item) {

        item = itemBean.putItem(itemId, item);

        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @DELETE
    @Path("{itemId}")
    public Response deleteItem(@PathParam("itemId") Integer itemId) {

        boolean deleted = itemBean.deleteItem(itemId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }




}
