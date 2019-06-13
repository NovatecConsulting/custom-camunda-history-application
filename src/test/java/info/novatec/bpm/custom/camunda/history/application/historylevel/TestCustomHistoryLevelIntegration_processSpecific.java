package info.novatec.bpm.custom.camunda.history.application.historylevel;

import static org.camunda.bpm.engine.test.assertions.cmmn.CmmnAwareTests.complete;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstance;
import org.camunda.bpm.engine.history.HistoricCaseInstance;
import org.camunda.bpm.engine.history.HistoricDecisionInstance;
import org.camunda.bpm.engine.history.HistoricExternalTaskLog;
import org.camunda.bpm.engine.history.HistoricIncident;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.configurations.EngineHistoryConfiguration;
import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;

/**
 * Test for {@link CustomHistoryLevelPlugin} on correct behavior on process specific configuration.
 * 
 * @author Ben Fuernrohr
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestCustomHistoryLevelIntegration_processSpecific extends AbsTestCustomHistoryLevelIntegration {

    @MockBean
    private EngineHistoryConfiguration engineHistoryArchiveConfigurationMock;

    @MockBean
    private ProcessHistoryConfiguration processHistoryArchiveConfiguration;

    @Before
    public void setUp() {
        // mock behavior for no history to be created engine wide whatsoever
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey(Mockito.anyString()))
            .willReturn(false);
        // mock behavior for every model to have specific configuration for all object types
        given(this.processHistoryArchiveConfiguration.isConfigurationSetForProcessDefinitionId(Mockito.anyString(),
            Mockito.anyString()))
                .willReturn(true);
        // mock default behavior: every process model specific configuration set to false
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            Mockito.anyString(),
            Mockito.anyString())).willReturn(false);

        // clean up history
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
            .completed()
            .list();
        if (!historicProcessInstances.isEmpty()) {
            this.historyService.deleteHistoricProcessInstancesBulk(historicProcessInstances
                .stream()
                .map(instance -> instance.getId())
                .collect(Collectors.toList()));
        }
    }

    @Test
    public void processInstances_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this.processDefinitionId(this.simpleTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId,
            "process-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        List<HistoricProcessInstance> processInstances = this.historyService.createHistoricProcessInstanceQuery()
            .list();
        assertEquals(0, processInstances.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId,
            "process-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        processInstances = this.historyService.createHistoricProcessInstanceQuery().list();
        assertEquals(1, processInstances.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    public void activityInstances_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this.processDefinitionId(this.simpleTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "activity-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        List<HistoricActivityInstance> activityInstances = this.historyService.createHistoricActivityInstanceQuery()
            .list();
        assertEquals(0, activityInstances.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "activity-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        activityInstances = this.historyService.createHistoricActivityInstanceQuery()
            .list();
        assertEquals(3, activityInstances.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    public void variables_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this.processDefinitionId(this.simpleTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "variable-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        List<HistoricVariableInstance> variableInstances = this.historyService.createHistoricVariableInstanceQuery()
            .list();
        assertEquals(0, variableInstances.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "variable-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        variableInstances = historyService.createHistoricVariableInstanceQuery().list();
        assertEquals(3, variableInstances.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    public void incidents_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this.processDefinitionId(this.simpleTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "incident")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        List<HistoricIncident> incidents = this.historyService.createHistoricIncidentQuery()
            .list();
        assertEquals(0, incidents.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "incident")).willReturn(true);
        this.runtimeService.startProcessInstanceByKey("simple-test-process");

        incidents = this.historyService.createHistoricIncidentQuery()
            .list();
        assertEquals(1, incidents.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    @Ignore // known issues, decisions can't be refered to process definitions that called them
    public void decisions_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this
            .processDefinitionId(this.simpleEvaluationTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "decision")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        List<HistoricDecisionInstance> decisionInstances = this.historyService.createHistoricDecisionInstanceQuery()
            .list();
        assertEquals(0, decisionInstances.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "decision")).willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        decisionInstances = this.historyService.createHistoricDecisionInstanceQuery()
            .list();
        assertEquals(1, decisionInstances.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    public void case_instance_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this.processDefinitionId(this.simpleCaseTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "case-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);
        complete(caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        List<HistoricCaseInstance> caseInstances = this.historyService.createHistoricCaseInstanceQuery()
            .list();
        assertEquals(0, caseInstances.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "case-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);
        complete(caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        caseInstances = this.historyService.createHistoricCaseInstanceQuery()
            .list();
        assertEquals(1, caseInstances.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    public void case_activity_instances_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this.processDefinitionId(this.simpleCaseTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "case-activity-instance"))
                .willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);
        complete(caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        List<HistoricCaseActivityInstance> caseInstances = this.historyService.createHistoricCaseActivityInstanceQuery()
            .list();
        assertEquals(0, caseInstances.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "case-activity-instance"))
                .willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);
        complete(caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        caseInstances = this.historyService.createHistoricCaseActivityInstanceQuery()
            .list();
        assertEquals(2, caseInstances.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }

    @Test
    public void external_task_log_should_be_written_to_history_according_to_process_specific_configuration() {
        String deploymentTestProcessDefinitionId = this
            .processDefinitionId(this.simpleExternalTaskTestProcessDefinitionKey);
        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "external-task")).willReturn(false);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        ExternalTask externalTask = externalTaskService.createExternalTaskQuery()
            .topicName("externalTask")
            .active()
            .list()
            .get(0);
        externalTaskService.fetchAndLock(1, "workerId").topic("externalTask", 1000).execute();
        externalTaskService.complete(externalTask.getId(), "workerId");

        List<HistoricExternalTaskLog> externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(0, externalTaskLogs.size());

        given(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            deploymentTestProcessDefinitionId, "external-task")).willReturn(true);
        this.runtimeService.startProcessInstanceById(deploymentTestProcessDefinitionId);

        externalTask = externalTaskService.createExternalTaskQuery()
            .topicName("externalTask")
            .active()
            .list()
            .get(0);
        externalTaskService.fetchAndLock(1, "workerId").topic("externalTask", 1000).execute();

        externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(1, externalTaskLogs.size());

        externalTaskService.complete(externalTask.getId(), "workerId");

        externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(2, externalTaskLogs.size());
        Mockito.verifyZeroInteractions(this.engineHistoryArchiveConfigurationMock);
    }
}