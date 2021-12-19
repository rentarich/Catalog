//package si.fri.rso.catalog.api.v1.health;
//
//import org.eclipse.microprofile.health.HealthCheck;
//import org.eclipse.microprofile.health.HealthCheckResponse;
//import org.eclipse.microprofile.health.Liveness;
//import si.fri.rso.catalog.models.dtos.Item;
//import si.fri.rso.catalog.services.beans.ItemBean;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Inject;
//import java.util.List;
//
//@Liveness
//@ApplicationScoped
//public class ReturnedItemsHealthCheck implements HealthCheck {
//    @Inject
//    private ItemBean itemBean;
//
//    @Override
//    public HealthCheckResponse call() {
//        if (itemBean.getItems()) {
//            return HealthCheckResponse.down(ReturnedItemsHealthCheck.class.getSimpleName());
//        }
//        return HealthCheckResponse.up(ReturnedItemsHealthCheck.class.getSimpleName());
//    }
//}
