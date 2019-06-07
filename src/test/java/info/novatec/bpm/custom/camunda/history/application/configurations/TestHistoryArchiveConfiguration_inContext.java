package info.novatec.bpm.custom.camunda.history.application.configurations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.configurations.EngineHistoryConfiguration;

/**
 * Testclass for {@link EngineHistoryConfiguration}. Tests for proper property-reading from given a application.yaml
 * (test-resource)
 * 
 * @author Ben Fuernrohr
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestHistoryArchiveConfiguration_inContext {

    /** Subject under test */
    @Autowired
    private EngineHistoryConfiguration engineHistoryConfiguration;

    @Test
    public void property_map_should_return_values_on_keys_according_to_configuration() {
        assertEquals(false, this.engineHistoryConfiguration.getConfigurationForKey("process-instance"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("activity-instance"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("task-instance"));
        assertEquals(false, this.engineHistoryConfiguration.getConfigurationForKey("variable-instance"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("incident"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("case-instance"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("case-activity-instance"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("decision"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("external-task"));
        assertEquals(true, this.engineHistoryConfiguration.getConfigurationForKey("user-operation-log"));
    }
}