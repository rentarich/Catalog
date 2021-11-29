package si.fri.rso.catalog.api.v1.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rso.catalog.services.config.RestProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class CustomHealthCheck implements HealthCheck {

    @Inject
    private RestProperties restProperties;

    @Override
    public HealthCheckResponse call() {
        if (restProperties.getMaintenance()) {
            return HealthCheckResponse.down(CustomHealthCheck.class.getSimpleName());
        }
        else {
            return HealthCheckResponse.up(CustomHealthCheck.class.getSimpleName());
        }
    }
}