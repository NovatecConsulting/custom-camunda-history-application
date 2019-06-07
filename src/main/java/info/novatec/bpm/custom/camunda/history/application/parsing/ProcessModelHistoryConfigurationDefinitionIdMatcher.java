package info.novatec.bpm.custom.camunda.history.application.parsing;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.persistence.deploy.Deployer;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;

/**
 * Matcher that Replaces initial process definition ids in a {@link ProcessHistoryConfiguration}-object with actual
 * deployment-ids upon process deployment.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class ProcessModelHistoryConfigurationDefinitionIdMatcher implements Deployer {

    @Autowired
    private ProcessHistoryConfiguration processHistoryConfiguration;

    @SuppressWarnings("unchecked")
    @Override
    public void deploy(DeploymentEntity deployment) {
        // copy list to avoid ConcurrentModificationException
        List<String> archivedProcesses = new ArrayList<String>(
            this.processHistoryConfiguration.getArchivedProcesses());
        List<ProcessDefinitionEntity> processDefinitionEntities = deployment.getDeployedArtifacts()
            .get(ProcessDefinitionEntity.class);
        // no need for checking if no process or case definitions are being deployed
        if (processDefinitionEntities != null && !processDefinitionEntities.isEmpty()) {
            for (String processDefinitionKey : archivedProcesses) {
                for (ProcessDefinitionEntity processDefinitionEntity : processDefinitionEntities) {
                    if (processDefinitionEntity.getKey() == processDefinitionKey) {
                        this.processHistoryConfiguration.updateProcessDefinitionId(processDefinitionKey,
                            processDefinitionEntity.getId());
                    }
                }
            }
        }
    }
}