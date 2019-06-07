package info.novatec.bpm.custom.camunda.history.application.historylevel;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.impl.dmn.entity.repository.DecisionDefinitionEntity;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventType;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExternalTaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.IncidentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import info.novatec.bpm.custom.camunda.history.application.configurations.EngineHistoryConfiguration;
import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;

/**
 * Custom History Level to allow a specified, configurable history filter for Camunda BPM.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class CustomHistoryLevel implements HistoryLevel {

    @Autowired
    private ProcessHistoryConfiguration processHistoryConfiguration;

    @Autowired
    private EngineHistoryConfiguration engineHistoryConfiguration;

    public CustomHistoryLevel() {
    }

    @Override
    public int getId() {
        return 24;
    }

    @Override
    public String getName() {
        return "customHistoryLevel";
    }

    @Override
    public boolean isHistoryEventProduced(HistoryEventType eventType, Object entity) {
        // null entity means that the method is called upon deployment to determine, whether a history event may ever be
        // produced for a given eventType
        // so always return true at this point to allow for educated filtering during runtime.
        if (entity == null) {
            return true;
        } else {
            // check for process-definition specific configuration
            String processDefinitionId = this.getProcessDefinitionId(entity);
            if (this.processHistoryConfiguration.isConfigurationSetForProcessDefinition(processDefinitionId,
                eventType.getEntityType())) {
                return this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                    processDefinitionId,
                    eventType.getEntityType());
            } else {
                // no configuration for this process-definition, fall back to enginewide configuration
                return this.engineHistoryConfiguration.getConfigurationForKey(eventType.getEntityType());
            }
        }
    }

    private String getProcessDefinitionId(Object entity) {
        if (entity instanceof ExecutionEntity) {
            return ((ExecutionEntity) entity).getProcessDefinitionId();
        } else if (entity instanceof VariableInstanceEntity) {
            return ((VariableInstanceEntity) entity).getExecution().getProcessDefinitionId();
        } else if (entity instanceof HistoryEvent) {
            return ((HistoryEvent) entity).getProcessDefinitionId();
        } else if (entity instanceof CaseExecutionEntity) {
            return getProcessDefinitionIdOfParent((CaseExecutionEntity) entity);
        } else if (entity instanceof TaskEntity) {
            String processDefinitionId = ((TaskEntity) entity).getProcessDefinitionId();
            // check if the task belongs to a process
            if (processDefinitionId != null)
                return processDefinitionId;
            else
                // so the task belongs to a case
                return getProcessDefinitionIdOfParent(((TaskEntity) entity).getCaseExecution());
        } else if (entity instanceof IncidentEntity) {
            return ((IncidentEntity) entity).getProcessDefinitionId();
        } else if (entity instanceof ExternalTaskEntity) {
            return ((ExternalTaskEntity) entity).getProcessDefinitionId();
        } else if (entity instanceof DecisionDefinitionEntity) {
            return ((DecisionDefinitionEntity) entity).getId();
        } else {
            throw new ProcessEngineException(
                "Unable to determine definition key for entity of class " + entity.getClass().getName());
        }
    }

    private String getProcessDefinitionIdOfParent(CaseExecutionEntity entity) {
        if (entity == null) {
            return null;
        }
        ExecutionEntity parentExecution = entity.getSuperExecution();
        if (parentExecution != null)
            return parentExecution.getProcessDefinitionId();
        else
            return getProcessDefinitionIdOfParent(entity.getParent());
    }
}