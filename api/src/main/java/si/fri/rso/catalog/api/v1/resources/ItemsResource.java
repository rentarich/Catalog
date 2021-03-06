package si.fri.rso.catalog.api.v1.resources;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.cdi.Log;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import si.fri.rso.catalog.models.dtos.ItemDTO;
import si.fri.rso.catalog.services.beans.BorrowBean;
import si.fri.rso.catalog.services.beans.ItemBean;
import si.fri.rso.catalog.services.beans.UserBean;
import si.fri.rso.catalog.services.config.RestProperties;
import javax.annotation.PostConstruct;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Log
@Path("items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemsResource {
    private com.kumuluz.ee.logs.Logger logger = LogManager.getLogger(ItemsResource.class.getName());
    private Logger log = Logger.getLogger(ItemsResource.class.getName());
    private Client httpClient;
    private String baseUrl;

    @Inject
    private ItemBean itemBean;

    @Inject
    private UserBean userBean;

    @Inject
    private BorrowBean borrowBean;

    @Inject
    private RestProperties restProperties;

    @Context
    protected UriInfo uriInfo;

//    @Inject
//    @DiscoverService(value= "recommendation-system")
//    private URL url;


    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        logger.info("Init bean: " + ItemsResource.class.getSimpleName() + " idBean: " + UUID.randomUUID().toString());
        baseUrl = ConfigurationUtil.getInstance().get("kumuluzee.server.base-url").orElse("N/A");
    }

//    @GET
//    @Path("discovery")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response discoveryInMethod() {
//        if(url != null) {
//            log.info(url.toString()+" success!!! IMAMO LINKE!!!!");
//        }
//
//        return Response.noContent().build();
//
//    }

    @GET
    @Operation(summary = "Get all items that are on Rentarich.", description = "Returns items.", tags = "items")
    @ApiResponses({
            @ApiResponse(description = "List of items", responseCode = "200", content = @Content( array = @ArraySchema(schema = @Schema(implementation =
                    ItemDTO.class)))),
            @ApiResponse(responseCode = "405", description = "Validation error.")
    })
    public Response getItems() {

        log.info(System.getenv().get("config_2_value"));
        logger.info("CONFIG VALUE: "+System.getenv().get("config_2_value"));
        logger.info("Get all items");
        List<ItemDTO> imageMetadata = itemBean.getItemsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(imageMetadata).build();

    }

    @GET
    @Operation(summary = "Get reccomended items for a person", description = "Returns reccomended items.", tags = "items")
    @ApiResponses({
            @ApiResponse(description = "List of reccomended items", responseCode = "200", content = @Content( array = @ArraySchema(schema = @Schema(implementation =
                    ItemDTO.class)))),
            @ApiResponse(responseCode = "405", description = "Validation error.")
    })
    @Path("person/{userId}/")
    public Response getReccomended(@PathParam("userId") Integer userId) throws IOException, UnirestException {
        List<ItemDTO> recommendedItems = userBean.getReccomended(userId);
        logger.info("Get reccomended items for person"+userId);
        return Response.status(Response.Status.OK).entity(recommendedItems).build();
    }

    @GET
    @Path("available")
    @Operation(summary = "Get available (not reserved or borrowed) items.", description = "Returns items that are currently not reserved or borrowed.", tags = "items")
    @ApiResponses({
            @ApiResponse(description = "List of available items", responseCode = "200", content = @Content( array = @ArraySchema(schema = @Schema(implementation =
                    ItemDTO.class)))),
            @ApiResponse(responseCode = "405", description = "Validation error.")
    })
    public Response getAvailableItems() {
        logger.info("get all available items");
        List<ItemDTO> items = itemBean.getAvailableItemsFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(items).build();


    }


    @GET
    @Operation(summary = "Get single item with id.", description = "Returns requested item.", tags = "items")
    @ApiResponses({
            @ApiResponse(description = "List of available items", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    ItemDTO.class))),
            @ApiResponse(responseCode = "405", description = "Validation error."),
            @ApiResponse(responseCode = "404", description = "Not Found.")
    })
    @Path("/{itemId}")
    public Response getItem(@PathParam("itemId") Integer itemId) {

        ItemDTO item = itemBean.getItem(itemId);
        logger.info("get item"+itemId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(item).build();
    }

    @Operation(description = "Add new item.", summary = "Adding item",
            tags = "items",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Item Created.", content = @Content(schema = @Schema(implementation =
                            ItemDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request."),

            })
    @POST
    public Response createItem(ItemDTO item) {


        if ((item.getTitle() == null || item.getDescription() == null || item.getCategory() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            item = itemBean.createItem(item);
        }

        return Response.status(Response.Status.CREATED).entity(item).build();

    }

    @Operation(description = "Update item.", summary = "Updating item item",
            tags = "items",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item updated."),
                    @ApiResponse(responseCode = "404", description = "Not Found.")

            })
    @PUT
    @Path("{itemId}")
    public Response putItem(@PathParam("itemId") Integer itemId,
                            ItemDTO item) {

        item = itemBean.putItem(itemId, item);

        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Operation(description = "Delete/Remove item.", summary = "Removing item",
            tags = "items",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Item removed."
                    ),
                    @ApiResponse(responseCode = "404", description = "Not Found.")})
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
