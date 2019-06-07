package info.novatec.bpm.custom.camunda.history.application.configurations;

import java.util.HashMap;

import org.camunda.bpm.engine.impl.history.event.HistoryEventType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class to allow external, engine wide configuration of the history level by storing configureable flags indicating
 * which elements are to be archived in the history database.
 * 
 * @author Ben Fuernrohr
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties("history")
public class EngineHistoryConfiguration {

    /** Map to hold the configuration flags */
    private HashMap<String, Boolean> configuration = new HashMap<String, Boolean>();

    /**
     * Returns the history-configuration set for a given entity type (see entityTypes of {@link HistoryEventType}),
     * indicating whether that entity is to be stored in the history or not. Will return {@link false} if no
     * configuration was set for that key.
     * 
     * @param key
     *            the entity type for which the configuration is requested.
     * @return the configuration set for the particular entity.
     */
    public boolean getConfigurationForKey(String key) {
        Object result = this.configuration.get(key);
        return result == null ? false : (boolean) result;
    }

    public HashMap<String, Boolean> getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(HashMap<String, Boolean> configuration) {
        this.configuration = configuration;
    }
}