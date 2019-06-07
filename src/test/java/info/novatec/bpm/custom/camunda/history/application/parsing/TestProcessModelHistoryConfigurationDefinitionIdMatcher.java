package info.novatec.bpm.custom.camunda.history.application.parsing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.camunda.bpm.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import info.novatec.bpm.custom.camunda.history.application.HistoryApplication;
import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;

/**
 * Class to test the matching of definition process definition ids to generated process definition ids in
 * {@link ProcessModelHistoryDefinitionIdMatcher}.
 * 
 * @author Ben Fuernrohr
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HistoryApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
public class TestProcessModelHistoryConfigurationDefinitionIdMatcher {

    @LocalServerPort
    private int port;

    @Autowired
    private ProcessHistoryConfiguration processHistoryArchiveConfiguration;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void configuration_should_be_added_on_autodeployment_with_matching_definitionId() {
        // process-definition for simple-test-process: process-instance: true, activity-instance: false,
        // variable-instance: true
        // get the definitionId of the newest deployment of the test-resources (i.e. simple-test-process:1:7)
        String processDefinitionId = this.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("simple-test-process")
            .latestVersion()
            .singleResult()
            .getId();

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
    public void deploying_another_version_of_same_process_should_create_another_configuration() {
        String oldProcessDefinitionId = this.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("simple-test-process")
            .latestVersion()
            .singleResult()
            .getId();

        this.repositoryService.createDeployment().addClasspathResource("bpmn/simple-test-process.bpmn").deploy();

        String newProcessDefinitionId = this.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("simple-test-process")
            .latestVersion()
            .singleResult()
            .getId();

        assertNotEquals(newProcessDefinitionId, oldProcessDefinitionId);
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            oldProcessDefinitionId,
            "process-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            oldProcessDefinitionId,
            "variable-instance"));
        assertFalse(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            oldProcessDefinitionId,
            "activity-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            newProcessDefinitionId,
            "process-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            newProcessDefinitionId,
            "variable-instance"));
        assertFalse(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            newProcessDefinitionId,
            "activity-instance"));
    }

    @Test
    public void deploying_another_version_of_same_process_via_rest_should_create_another_configuration() {
        String oldProcessDefinitionId = this.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("simple-test-process")
            .latestVersion()
            .singleResult()
            .getId();

        this.deploySimpleTestProcessViaRest();

        String newProcessDefinitionId = this.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("simple-test-process")
            .latestVersion()
            .singleResult()
            .getId();

        assertNotEquals(newProcessDefinitionId, oldProcessDefinitionId);
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            oldProcessDefinitionId,
            "process-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            oldProcessDefinitionId,
            "variable-instance"));
        assertFalse(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            oldProcessDefinitionId,
            "activity-instance"));

        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            newProcessDefinitionId,
            "process-instance"));
        assertTrue(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            newProcessDefinitionId,
            "variable-instance"));
        assertFalse(this.processHistoryArchiveConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            newProcessDefinitionId,
            "activity-instance"));
    }

    private void deploySimpleTestProcessViaRest() throws HttpServerErrorException {
        ClassPathResource bar = new ClassPathResource("/bpmn/simple-test-process.bpmn");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("deployment-name", "simple-test-process.bpmn");
        parts.add("deployment-source", "simple-test-process.bpmn");
        parts.add("enable-duplicate-filtering", "true");
        parts.add("deployment-name", "test-deployment");
        parts.add("data", bar);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);
        restTemplate.exchange("/rest/deployment/create", HttpMethod.POST, request, String.class);
    }
}