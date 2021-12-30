package si.fri.rso.catalog.api.v1.health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rso.catalog.services.beans.ItemBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class ItemsHealthCheck implements HealthCheck {
    @Inject
    private ItemBean itemBean;

    @Override
    public HealthCheckResponse call() {
        try {
            itemBean.getItems();
        } catch (Exception e){
            return HealthCheckResponse.down(ItemsHealthCheck.class.getSimpleName());
        }

        return HealthCheckResponse.up(ItemsHealthCheck.class.getSimpleName());

    }
}
