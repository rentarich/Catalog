package si.fri.rso.catalog.services.beans;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import java.time.temporal.ChronoUnit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import si.fri.rso.catalog.models.converters.ItemConverter;
import si.fri.rso.catalog.models.dtos.ItemDTO;
import si.fri.rso.catalog.models.entities.Item;
import si.fri.rso.catalog.services.config.RestProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserBean {
    private Logger log = Logger.getLogger(ItemBean.class.getName());
    private String idBean;
    private Client httpClient;
    private String baseUrl;
    private ItemBean itemBean;
    private com.kumuluz.ee.logs.Logger logger = LogManager.getLogger(ItemBean.class.getName());

    @Inject
    private RestProperties restProperties;

    @PostConstruct
    private void init(){
        idBean = UUID.randomUUID().toString();
        httpClient = ClientBuilder.newClient();
        log.info("Base url of recommendation system: " + " base url: " + baseUrl);
        log.info("Init bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);
        logger.info("Base url of recommendation system: " + " base url: " + baseUrl);
        logger.info("Init bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);

    }

    @Inject
    private BorrowBean borrowBean;

    @PreDestroy
    private void destroy(){
        log.info("Deinit bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);
        logger.info("Deinit bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);
    }

    @PersistenceContext(unitName = "item-jpa")
    private EntityManager em;

    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getStandard")
    public List<ItemDTO> getReccomended(Integer userId) throws UnirestException {
        logger.info("Connecting with ms for reccomendation");
        baseUrl= "http://"+restProperties.getUrl()+"/v1/persons/";
        try {
            HttpResponse<String> response = Unirest.get("http://"+restProperties.getUrl()+"/recommendation/v1/persons/" + userId.toString() + "/recommend").asString();
            log.info("Base url of recommendation system: " + " base url: " + baseUrl);
            logger.info("Base url of recommendation system: " + " base url: " + baseUrl);
            log.info(response.getBody());
            Type listType = new TypeToken<List<ItemDTO>>() {
            }.getType();
            List<ItemDTO> items = new Gson().fromJson(response.getBody(), listType);
            return items;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public List<ItemDTO> getStandard(Integer userId) {
        log.info("Searching for alternative recomendation -- setting default");
        logger.info("Searching for alternative recomendation FALLBACK METHOD -- setting default reccomendation");
        //return itemBean.getItems();
        TypedQuery<Item> query = em.createNamedQuery("ItemEntity.getAll", Item.class);

        List<ItemDTO> items = JPAUtils.queryEntities(em, Item.class).stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());
        //log.info(items.stream().toArray().toString());
        //return Collections.emptyList();
        return items;
    }

}
