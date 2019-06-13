package info.novatec.bpm.custom.camunda.history.application.historylevel;

import static org.camunda.bpm.engine.test.assertions.cmmn.CmmnAwareTests.complete;
import static org.junit.Assert.assertEquals;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;

/**
 * Integration test for the functionality of {@link CustomHistoryLevelPlugin}. Applies the configuration set in
 * application.yaml and the respective models.
 * 
 * @author Ben Fuernrohr
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class)
@DirtiesContext
public class TestCustomHistoryLevelIntegration extends AbsTestCustomHistoryLevelIntegration {

    @Before
    public void setUp() {
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
    public void process_model_configuration_should_trump_enginewide_configuration_if_existant() {
        // application.yaml: process-instance: false, activity-instance: false, variable-instance: false, incident: true
        // process-definition: process-instance: true, activity-instance: false, variable-instance: true
        // should result in all true but activity-instance for this process-definition

        this.runtimeService
            .startProcessInstanceById(processDefinitionId(this.simpleTestProcessDefinitionKey));

        List<HistoricProcessInstance> processInstances = this.historyService.createHistoricProcessInstanceQuery()
            .list();
        List<HistoricActivityInstance> activityInstances = this.historyService.createHistoricActivityInstanceQuery()
            .list();
        List<HistoricVariableInstance> variableInstances = this.historyService.createHistoricVariableInstanceQuery()
            .list();
        List<HistoricIncident> incidents = this.historyService.createHistoricIncidentQuery()
            .list();

        assertEquals(1, processInstances.size());
        assertEquals(0, activityInstances.size());
        assertEquals(3, variableInstances.size());
        assertEquals(1, incidents.size());
    }

    @Test
    public void no_configuration_on_process_model_should_fall_back_to_enginewide_configuration() {
        // application.yaml: process-instance: false, activity-instance: false, variable-instance: false, decision: true

        this.runtimeService
            .startProcessInstanceById(processDefinitionId(simpleEvaluationTestProcessDefinitionKey));

        List<HistoricProcessInstance> processInstances = this.historyService.createHistoricProcessInstanceQuery()
            .list();
        List<HistoricActivityInstance> activityInstances = this.historyService.createHistoricActivityInstanceQuery()
            .list();
        List<HistoricVariableInstance> variableInstances = this.historyService.createHistoricVariableInstanceQuery()
            .list();
        List<HistoricDecisionInstance> decisionInstances = this.historyService.createHistoricDecisionInstanceQuery()
            .list();

        assertEquals(0, processInstances.size());
        assertEquals(0, activityInstances.size());
        assertEquals(0, variableInstances.size());
        assertEquals(1, decisionInstances.size());
    }

    @Test
    public void external_task_log_should_be_written_to_history_according_to_process_specific_configuration() {
        // process-definition: external-task: false

        this.runtimeService
            .startProcessInstanceById(processDefinitionId(simpleExternalTaskTestProcessDefinitionKey));

        ExternalTask externalTask = externalTaskService.createExternalTaskQuery()
            .topicName("externalTask")
            .active()
            .list()
            .get(0);
        externalTaskService.fetchAndLock(1, "workerId").topic("externalTask", 1000).execute();

        List<HistoricExternalTaskLog> externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(0, externalTaskLogs.size());

        externalTaskService.complete(externalTask.getId(), "workerId");

        externalTaskLogs = this.historyService.createHistoricExternalTaskLogQuery()
            .list();
        assertEquals(0, externalTaskLogs.size());
    }

    @Test
    public void case_entities_should_be_written_to_history_according_to_process_specific_configuration() {
        // case-instance: true, case-activity-instance: false
        this.runtimeService
            .startProcessInstanceById(processDefinitionId(simpleCaseTestProcessDefinitionKey));
        complete(this.caseService.createCaseExecutionQuery().activityId("HumanTask1").active().list().get(0));

        List<HistoricCaseInstance> caseInstances = this.historyService.createHistoricCaseInstanceQuery()
            .list();
        assertEquals(1, caseInstances.size());

        List<HistoricCaseActivityInstance> caseActivityInstances = this.historyService
            .createHistoricCaseActivityInstanceQuery()
            .list();
        assertEquals(0, caseActivityInstances.size());
    }
}