package si.fri.rso.catalog.services.beans;


import com.kumuluz.ee.logs.LogManager;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.catalog.models.entities.Borrow;
import si.fri.rso.catalog.models.entities.Item;
import si.fri.rso.catalog.models.entities.Person;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
@Timed(name= "itemsCatalog")
public class BorrowBean {
    private Logger log = Logger.getLogger(BorrowBean.class.getName());
    private String idBean;
    private com.kumuluz.ee.logs.Logger logger = LogManager.getLogger(ItemBean.class.getName());

    @PostConstruct
    private void init(){
        idBean = UUID.randomUUID().toString();
        log.info("Init bean: " + BorrowBean.class.getSimpleName() + " idBean: " + idBean);
        logger.info("Init bean: " + BorrowBean.class.getSimpleName() + " idBean: " + idBean);
    }

    @PreDestroy
    private void destroy(){
        log.info("Deinit bean: " + BorrowBean.class.getSimpleName() + " idBean: " + idBean);
        logger.info("Deinit bean: " + BorrowBean.class.getSimpleName() + " idBean: " + idBean);
    }

    @PersistenceContext(unitName = "item-jpa")
    private EntityManager em;

    public List getBorrows(){
        return em.createNamedQuery("Borrow.getAll").getResultList();
    }

    public List<Borrow> getPersonsBorrows(Person person) {
        TypedQuery<Borrow> query= em.createNamedQuery("Borrow.getBorrowForPerson", Borrow.class);
        return query.setParameter("person",person).getResultList();
    }

    public List<Borrow> getItemBorrows(Item item) {
        TypedQuery<Borrow> query= em.createNamedQuery("Borrow.getBorrowForItem", Borrow.class);
        return query.setParameter("item",item).getResultList();
    }

    public List<Item> getBorrowedItems() {
        logger.info("get all borrowed items");
        TypedQuery<Borrow> query= em.createNamedQuery("Borrow.getReservedOrBorrowedItems", Borrow.class);
        List<Item> itemsBor = new ArrayList<>();
        for (Borrow borrow : query.getResultList()) {
            itemsBor.add(borrow.getItem());
        }
        return itemsBor;
    }
}