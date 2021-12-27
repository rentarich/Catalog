package si.fri.rso.catalog.services.beans;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.catalog.models.converters.ItemConverter;
import si.fri.rso.catalog.models.dtos.ItemDTO;
import si.fri.rso.catalog.models.entities.Item;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ApplicationScoped
public class ItemBean {
    private Logger log = Logger.getLogger(ItemBean.class.getName());
    private com.kumuluz.ee.logs.Logger logger = LogManager.getLogger(ItemBean.class.getName());
    private String idBean;

    @PostConstruct
    private void init(){
        idBean = UUID.randomUUID().toString();
        log.info("Init bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);
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




    public boolean getItems() {
        List<ItemDTO> items = em.createNamedQuery("ItemEntity.getAll", Item.class).getResultList().stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());
        return items.size() > 0;
    }

    public List<ItemDTO> getItemss() {
        List<ItemDTO> items = em.createNamedQuery("ItemEntity.getAll", Item.class).getResultList().stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());
        return items;
    }

    public List<ItemDTO> getAvailableItemsFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        List<ItemDTO> items = JPAUtils.queryEntities(em, Item.class, queryParameters).stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());

        List<ItemDTO> bor = borrowBean.getBorrowedItems().stream().map(ItemConverter::toDto).collect(Collectors.toList());
        log.info(bor.stream().map(borrowed -> borrowed.getId()).collect(Collectors.toList()).toString());
        List<Integer> borrowedIds = bor.stream().map(borrowed -> borrowed.getId()).collect(Collectors.toList());
        items.removeIf(item -> borrowedIds.contains(item.getId()));

        return items;
    }


   public ItemDTO getItem(Integer id) {

        Item itemEntity = em.find(Item.class, id);

        if (itemEntity == null) {
            throw new NotFoundException();
        }

        ItemDTO item = ItemConverter.toDto(itemEntity);

        return item;
    }

    public List<ItemDTO> getItemsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Item.class, queryParameters).stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ItemDTO createItem(ItemDTO item) {

        Item itemEntity = ItemConverter.toEntity(item);

        em.persist(itemEntity);

        return item;
    }

    @Transactional
    public ItemDTO putItem(Integer id, ItemDTO item) {

        Item c = em.find(Item.class, id);

        if (c == null) {
            return null;
        }

        Item updatedItemEntity = ItemConverter.toEntity(item);


        updatedItemEntity.setId(c.getId());
        updatedItemEntity = em.merge(updatedItemEntity);

        return ItemConverter.toDto(updatedItemEntity);
    }

    @Transactional
    public boolean deleteItem(Integer id) {

        Item item = em.find(Item.class, id);

        if (item != null) {

            em.remove(item);

        }
        else {
            return false;
        }

        return true;
    }

//    public List<Item> getItemsFilterCategory(UriInfo uriInfo, String category) {
//        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
//                .build();
//        TypedQuery<ItemEntity> query = em.createNamedQuery("ItemEntity.getCategory", ItemEntity.class);
//
//        List<Item> items = JPAUtils.queryEntities(em, ItemEntity.class, queryParameters).stream()
//                .map(ItemConverter::toDto).collect(Collectors.toList());
//
//        items.removeIf(item -> !item.getCategory().equalsIgnoreCase(category));
//
//        return items;
//
//    }
}

