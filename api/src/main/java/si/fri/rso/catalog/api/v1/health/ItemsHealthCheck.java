package si.fri.rso.catalog.api.v1.health;

import org.checkerframework.checker.units.qual.Length;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rso.catalog.models.dtos.Item;
import si.fri.rso.catalog.services.beans.ItemBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Liveness
@ApplicationScoped
public class ItemsHealthCheck implements HealthCheck {
    @Inject
    private ItemBean itemBean;

    @Override
    public HealthCheckResponse call() {
        List<Item> items = null;
        try {
            items = itemBean.getItems();
        } catch (Exception e){
            return HealthCheckResponse.down(ItemsHealthCheck.class.getSimpleName());
        }

        return HealthCheckResponse.up(ItemsHealthCheck.class.getSimpleName());

    }
}
