package info.novatec.bpm.custom.camunda.history.application.parsing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;
import info.novatec.bpm.custom.camunda.history.application.parsing.ProcessModelHistoryConfigurationDefinitionIdMatcher;
import info.novatec.bpm.custom.camunda.history.application.parsing.ProcessModelHistoryConfigurationParseListener;

/**
 * Class to test the parsing of history-specific extension parameters in bpmn files done by
 * {@link ProcessModelHistoryConfigurationParseListener}.
 * 
 * @author Ben Fuernrohr
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestProcessModelHistoryConfigurationParseListener {

    // mock behavior where definition ids are not matched to actual deployment ids, mock does nothing by default
    @MockBean
    ProcessModelHistoryConfigurationDefinitionIdMatcher processModelHistoryExtensionDefinitionIdMatcher;

    @Autowired
    private ProcessHistoryConfiguration processHistoryArchiveConfiguration;

    @Test
    public void bpmn_configuration_should_be_added_on_autodeployment_with_original_definitionId() {
        // process-definition for simple-test-process: process-instance: true, activity-instance: false,
        // variable-instance: true
        String processDefinitionId = "simple-test-process";

        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            processDefinitionId,
            "process-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            processDefinitionId,
            "variable-instance"));
        assertFalse(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            processDefinitionId,
            "activity-instance"));
    }

    @Test
    public void bpmn_configuration_should_be_added_on_manual_deployment_with_original_definitionId() {
        // process-definition for simple-test-process: process-instance: true, activity-instance: false,
        // variable-instance: true
        String processDefinitionId = "simple-test-process";

        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            processDefinitionId,
            "process-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            processDefinitionId,
            "variable-instance"));
        assertFalse(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            processDefinitionId,
            "activity-instance"));
    }
}
