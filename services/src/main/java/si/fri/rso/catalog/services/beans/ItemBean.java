package si.fri.rso.catalog.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.catalog.models.converters.ItemConverter;
import si.fri.rso.catalog.models.dtos.Item;
import si.fri.rso.catalog.models.entities.ItemEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
    private String idBean;

    @PostConstruct
    private void init(){
        idBean = UUID.randomUUID().toString();
        log.info("Init bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);
    }

    @PreDestroy
    private void destroy(){
        log.info("Deinit bean: " + ItemBean.class.getSimpleName() + " idBean: " + idBean);
    }

    @PersistenceContext(unitName = "item-jpa")
    private EntityManager em;

    public List<Item> getItems() {

        TypedQuery<ItemEntity> query = em.createNamedQuery(
                "ItemEntity.getAll", ItemEntity.class);

        List<ItemEntity> resultList = query.getResultList();

        return resultList.stream().map(ItemConverter::toDto).collect(Collectors.toList());

    }

   public Item getItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);

        if (itemEntity == null) {
            throw new NotFoundException();
        }

        Item item = ItemConverter.toDto(itemEntity);

        return item;
    }

    public List<Item> getItemsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ItemEntity.class, queryParameters).stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Item createItem(Item item) {

        ItemEntity itemEntity = ItemConverter.toEntity(item);

        em.persist(itemEntity);

        return item;
    }

    @Transactional
    public Item putItem(Integer id, Item item) {

        ItemEntity c = em.find(ItemEntity.class, id);

        if (c == null) {
            return null;
        }

        ItemEntity updatedItemEntity = ItemConverter.toEntity(item);


        updatedItemEntity.setId(c.getId());
        updatedItemEntity = em.merge(updatedItemEntity);

        return ItemConverter.toDto(updatedItemEntity);
    }

    @Transactional
    public boolean deleteItem(Integer id) {

        ItemEntity item = em.find(ItemEntity.class, id);

        if (item != null) {

            em.remove(item);

        }
        else {
            return false;
        }

        return true;
    }

    public List<Item> getItemsFilterCategory(UriInfo uriInfo, String category) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        List<Item> items = JPAUtils.queryEntities(em, ItemEntity.class, queryParameters).stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());

        items.removeIf(item -> !item.getCategory().equalsIgnoreCase(category));

        return items;

    }
}

