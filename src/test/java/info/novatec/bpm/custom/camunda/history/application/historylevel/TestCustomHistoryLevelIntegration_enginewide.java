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
import info.novatec.bpm.custom.camunda.history.application.historylevel.CustomHistoryLevel;

/**
 * Integrationtest for {@link CustomHistoryLevel} on correct behavior for enginewide configuration.
 * 
 * @author Ben Fuernrohr
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestCustomHistoryLevelIntegration_enginewide extends AbsTestCustomHistoryLevelIntegration {

    @MockBean
    private EngineHistoryConfiguration engineHistoryArchiveConfigurationMock;

    @MockBean
    private ProcessHistoryConfiguration processHistoryArchiveConfiguration;

    @Before
    public void setUp() {
        // mock behavior for no process definition specific configuration whatsoever
        given(this.processHistoryArchiveConfiguration.isConfigurationSetForProcessDefinition(Mockito.anyString(),
            Mockito.anyString()))
                .willReturn(false);

        // clean up history
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
            .list();
        if (!historicProcessInstances.isEmpty()) {
            this.historyService.deleteHistoricProcessInstancesBulk(historicProcessInstances
                .stream()
                .map(instance -> instance.getId())
                .collect(Collectors.toList()));
        }
    }

    @Test
    public void processInstances_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("process-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        List<HistoricProcessInstance> processInstances = this.historyService.createHistoricProcessInstanceQuery()
            .list();
        assertEquals(0, processInstances.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("process-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        processInstances = this.historyService.createHistoricProcessInstanceQuery().list();
        assertEquals(1, processInstances.size());
    }

    @Test
    public void activityInstances_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("activity-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        List<HistoricActivityInstance> activityInstances = this.historyService.createHistoricActivityInstanceQuery()
            .list();
        assertEquals(0, activityInstances.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("activity-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        activityInstances = this.historyService.createHistoricActivityInstanceQuery()
            .list();
        assertEquals(3, activityInstances.size());
    }

    @Test
    public void variables_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("variable-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        List<HistoricVariableInstance> variableInstances = this.historyService.createHistoricVariableInstanceQuery()
            .list();
        assertEquals(0, variableInstances.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("variable-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        variableInstances = historyService.createHistoricVariableInstanceQuery().list();
        assertEquals(3, variableInstances.size());
    }

    @Test
    public void incidents_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("incident")).willReturn(false);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        List<HistoricIncident> incidents = this.historyService.createHistoricIncidentQuery()
            .list();
        assertEquals(0, incidents.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("incident")).willReturn(true);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleTestProcessDefinitionKey));

        incidents = this.historyService.createHistoricIncidentQuery()
            .list();
        assertEquals(1, incidents.size());
    }

    @Test
    public void decisions_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("decision")).willReturn(false);
        this.runtimeService
            .startProcessInstanceById(this.processDefinitionId(this.simpleEvaluationTestProcessDefinitionKey));

        List<HistoricDecisionInstance> decisionInstances = this.historyService.createHistoricDecisionInstanceQuery()
            .list();
        assertEquals(0, decisionInstances.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("decision")).willReturn(true);
        this.runtimeService
            .startProcessInstanceById(this.processDefinitionId(this.simpleEvaluationTestProcessDefinitionKey));

        decisionInstances = this.historyService.createHistoricDecisionInstanceQuery()
            .list();
        assertEquals(1, decisionInstances.size());
    }

    @Test
    public void case_instance_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("case-instance")).willReturn(false);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleCaseTestProcessDefinitionKey));
        complete(this.caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        List<HistoricCaseInstance> caseInstances = this.historyService.createHistoricCaseInstanceQuery()
            .list();
        assertEquals(0, caseInstances.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("case-instance")).willReturn(true);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleCaseTestProcessDefinitionKey));
        complete(this.caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        caseInstances = this.historyService.createHistoricCaseInstanceQuery()
            .list();
        assertEquals(1, caseInstances.size());
    }

    @Test
    public void case_activity_instances_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("case-activity-instance"))
            .willReturn(false);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleCaseTestProcessDefinitionKey));
        complete(this.caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        List<HistoricCaseActivityInstance> caseActivityInstances = this.historyService
            .createHistoricCaseActivityInstanceQuery()
            .list();
        assertEquals(0, caseActivityInstances.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("case-activity-instance"))
            .willReturn(true);
        this.runtimeService.startProcessInstanceById(this.processDefinitionId(this.simpleCaseTestProcessDefinitionKey));
        complete(this.caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        caseActivityInstances = this.historyService.createHistoricCaseActivityInstanceQuery()
            .list();
        assertEquals(2, caseActivityInstances.size());
    }

    @Test
    public void external_task_log_should_be_written_to_history_according_to_configuration() {
        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("external-task")).willReturn(false);
        this.runtimeService
            .startProcessInstanceById(this.processDefinitionId(this.simpleExternalTaskTestProcessDefinitionKey));

        ExternalTask externalTask = externalTaskService.createExternalTaskQuery()
            .topicName("externalTask")
            .active()
            .list()
            .get(0);
        this.externalTaskService.fetchAndLock(1, "workerId").topic("externalTask", 1000).execute();
        this.externalTaskService.complete(externalTask.getId(), "workerId");

        List<HistoricExternalTaskLog> externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(0, externalTaskLogs.size());

        given(this.engineHistoryArchiveConfigurationMock.getConfigurationForKey("external-task")).willReturn(true);
        this.runtimeService
            .startProcessInstanceById(this.processDefinitionId(this.simpleExternalTaskTestProcessDefinitionKey));

        externalTask = externalTaskService.createExternalTaskQuery()
            .topicName("externalTask")
            .active()
            .list()
            .get(0);
        this.externalTaskService.fetchAndLock(1, "workerId").topic("externalTask", 1000).execute();

        externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(1, externalTaskLogs.size());

        this.externalTaskService.complete(externalTask.getId(), "workerId");

        externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(2, externalTaskLogs.size());
    }
}