package info.novatec.bpm.custom.camunda.history.application.parsing;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.persistence.deploy.Deployer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.parsing.ProcessModelHistoryConfigurationDefinitionIdMatcher;
import info.novatec.bpm.custom.camunda.history.application.parsing.ProcessModelHistoryConfigurationParseListener;
import info.novatec.bpm.custom.camunda.history.application.parsing.ProcessModelHistoryConfigurationParsePlugin;

/**
 * Test for {@link ProcessModelHistoryConfigurationParsePlugin}. Ensures the plugin registers parser and matcher with
 * the process engine.
 * 
 * @author Ben Fuernrohr
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestProcessModelHistoryConfigurationParsePlugin {

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void parse_listener_should_be_registered_in_processengine() {
        ProcessEngineConfiguration processEngineConfiguration = this.processEngine.getProcessEngineConfiguration();
        assertTrue(processEngineConfiguration instanceof ProcessEngineConfigurationImpl);

        List<BpmnParseListener> parseListenerList = ((ProcessEngineConfigurationImpl) processEngineConfiguration)
            .getCustomPreBPMNParseListeners();

        Condition<BpmnParseListener> parseListenerCondition = new Condition<BpmnParseListener>() {

            @Override
            public boolean matches(BpmnParseListener comparator) {
                return comparator.getClass() == ProcessModelHistoryConfigurationParseListener.class;
            }
        };

        Assertions.assertThat(parseListenerList).areExactly(1, parseListenerCondition);
    }

    @Test
    public void definition_matcher_should_be_registered_in_processengine() {
        ProcessEngineConfiguration processEngineConfiguration = this.processEngine.getProcessEngineConfiguration();
        assertTrue(processEngineConfiguration instanceof ProcessEngineConfigurationImpl);

        List<Deployer> postDeployerList = ((ProcessEngineConfigurationImpl) processEngineConfiguration)
            .getCustomPostDeployers();

        Condition<Deployer> postDeployerCondition = new Condition<Deployer>() {

            @Override
            public boolean matches(Deployer comparator) {
                return comparator.getClass() == ProcessModelHistoryConfigurationDefinitionIdMatcher.class;
            }
        };

        Assertions.assertThat(postDeployerList).areExactly(1, postDeployerCondition);
    }
}