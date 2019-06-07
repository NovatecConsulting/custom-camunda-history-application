package info.novatec.bpm.custom.camunda.history.application.historylevel;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.historylevel.CustomHistoryLevel;

/**
 * Parent test class for integrationtests of {@link CustomHistoryLevel} as used in the
 * {@link HistoryApplication}-application.
 * 
 * @author Ben Fuernrohr
 */
public abstract class AbsTestCustomHistoryLevelIntegration {

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected CaseService caseService;

    @Autowired
    protected ExternalTaskService externalTaskService;

    @Autowired
    protected RepositoryService repositoryService;

    /** DeploymentKey of simple-test-process */
    protected final String simpleTestProcessDefinitionKey = "simple-test-process";

    /** DeploymentKey of simple-external-task-test-process */
    protected final String simpleExternalTaskTestProcessDefinitionKey = "simple-external-task-test-process";

    /** DeploymentKey of simple-evaluation-test-process */
    protected final String simpleEvaluationTestProcessDefinitionKey = "simple-evaluation-test-process";

    /** DeploymentKey of simple-case-test-process */
    protected final String simpleCaseTestProcessDefinitionKey = "simple-case-test-process";

    /**
     * Get the definitionIds of an actual deployment of a test-processes (i.e. simple-test-process:1:7) from their
     * definition key
     * 
     * @param processDefinitionKey
     *            the definition key as defined in the bpmn-file.
     * @return the most recent rocessDefinitionId that a process with this key was assigned on a deployment.
     */
    protected String processDefinitionId(String processDefinitionKey) {
        return this.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .singleResult()
            .getId();
    }
}