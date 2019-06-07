package info.novatec.bpm.custom.camunda.history.application.configurations;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import info.novatec.bpm.custom.camunda.history.application.configurations.EngineHistoryConfiguration;

/**
 * Testclass for {@link EngineHistoryConfiguration}.
 * 
 * @author Ben Fuernrohr
 */
public class TestHistoryArchiveConfiguration {

    /** Subject under test */
    private EngineHistoryConfiguration engineHistoryConfiguration = new EngineHistoryConfiguration();

    @Test
    public void setting_configuration_should_allow_to_retrieve_same_configuration() {
        HashMap<String, Boolean> keyValueMap = new HashMap<String, Boolean>();
        keyValueMap.put("process-instance", false);
        keyValueMap.put("activity-instance", true);
        keyValueMap.put("decision", false);
        keyValueMap.put("incident", true);

        this.engineHistoryConfiguration.setConfiguration(keyValueMap);

        assertEquals(false, this.engineHistoryConfiguration.getConfigurationForKey("process-instance"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("activity-instance"));
        assertEquals(false, this.engineHistoryConfiguration.getConfigurationForKey("decision"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("incident"));

        // configurations that were not set should be null
        assertEquals(false, this.engineHistoryConfiguration.getConfigurationForKey("task-instance"));
        assertEquals(false, this.engineHistoryConfiguration.getConfigurationForKey("randomProperty"));
    }
}