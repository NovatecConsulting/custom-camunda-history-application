package info.novatec.bpm.custom.camunda.history.application.historylevel;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.historylevel.CustomHistoryLevel;
import info.novatec.bpm.custom.camunda.history.application.historylevel.CustomHistoryLevelPlugin;

/**
 * Test for {@link CustomHistoryLevelPlugin}. Ensures the plugin and the historylevel are registered with the process
 * engine.
 * 
 * @author Ben Fuernrohr
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestCustomHistoryLevelPlugin {

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void history_level_should_be_registered_in_processengine() {
        ProcessEngineConfiguration processEngineConfiguration = this.processEngine.getProcessEngineConfiguration();
        assertTrue(processEngineConfiguration instanceof ProcessEngineConfigurationImpl);

        List<HistoryLevel> historyLevelList = ((ProcessEngineConfigurationImpl) processEngineConfiguration)
            .getCustomHistoryLevels();

        Condition<HistoryLevel> historyLevelCondition = new Condition<HistoryLevel>() {

            @Override
            public boolean matches(HistoryLevel comparator) {
                return comparator.getClass() == CustomHistoryLevel.class;
            }
        };

        Assertions.assertThat(historyLevelList).areExactly(1, historyLevelCondition);

        HistoryLevel historyLevel = ((ProcessEngineConfigurationImpl) processEngineConfiguration).getHistoryLevel();
        assertTrue(historyLevel instanceof CustomHistoryLevel);
    }
}
