package si.fri.rso.catalog.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("rest-properties")
public class RestProperties {

    @ConfigValue(value = "maintenance", watch = true)
    private boolean maintenance;


    public boolean getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean config_1_value) {
        this.maintenance = config_1_value;
    }
}
