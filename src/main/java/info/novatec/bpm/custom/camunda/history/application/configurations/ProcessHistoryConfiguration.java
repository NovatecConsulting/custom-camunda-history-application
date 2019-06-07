package info.novatec.bpm.custom.camunda.history.application.configurations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.history.event.HistoryEventType;
import org.springframework.stereotype.Component;

/**
 * Class to allow the process-model specific configuration of the history level. Stores configureable flags indicating
 * which elements are to be archived in the history database for a process definition.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class ProcessHistoryConfiguration {

    /** Map to hold all configurations for all configured processes */
    private HashMap<String, Map<String, Boolean>> processConfigurationMap = new HashMap<String, Map<String, Boolean>>();

    /** List to hold the process definition ids of all configured processes */
    private List<String> configuredProcesses = new ArrayList<String>();

    /**
     * Adds a key-value-pair to the history-configuration of a single process-definition. If no configuration has yet
     * been created for this process-definition, one will be created.
     * 
     * @param processDefinitionKey
     *            the key of process-definition for which the configuration is intended
     * @param historyConfigurationKey
     *            the key of the entity type (see entityTypes of {@link HistoryEventType}) that this configuration is
     *            intended for
     * @param historyConfigurationValue
     *            the boolean value of the intended configuration
     */
    public void addConfigToProcessArchive(String processDefinitionKey, String historyConfigurationKey,
        boolean historyConfigurationValue) {
        Map<String, Boolean> processDefinitionMap = this.processConfigurationMap.get(processDefinitionKey);
        if (processDefinitionMap == null) {
            // configuration has not been initialized yet for this process definition
            processDefinitionMap = new HashMap<String, Boolean>();
            this.processConfigurationMap.put(processDefinitionKey, processDefinitionMap);
            this.configuredProcesses.add(processDefinitionKey);
        }
        processDefinitionMap.put(historyConfigurationKey, historyConfigurationValue);
    }

    /**
     * Returns the history-configuration set for a given entity type (see entityTypes of {@link HistoryEventType}) and
     * process definition id, indicating whether that entity is to be stored in the history or not.
     * 
     * @param processDefinitionId
     *            the key of the process definition for which the configuration is requested.
     * @param entityKey
     *            the entity type for which the configuration is requested.
     * @return the configuration set for the particular entity and process definition.
     */
    public Boolean getConfigurationForProcessDefinitionIdAndEntityKey(String processDefinitionId, String entityKey) {
        Map<String, Boolean> processDefinitionMap = this.processConfigurationMap.get(processDefinitionId);
        if (processDefinitionMap != null) {
            return processDefinitionMap.get(entityKey);
        }
        return null;
    }

    /**
     * Returns whether or not a history-configuration has been set for a given process-definition-id and a certain
     * entityType.
     * 
     * @param processDefinitionId
     *            the process-definition-key.
     * @param entityKey
     *            the entity type for which the configuration is requested.
     * @return {@link true} if a history-configuration has been set. Otherwise, {@link false}.
     */
    public boolean isConfigurationSetForProcessDefinitionId(String processDefinitionId, String entityKey) {
        Map<String, Boolean> processDefinitionMap = this.processConfigurationMap.get(processDefinitionId);
        if (processDefinitionMap == null)
            return false;
        else if (processDefinitionMap.get(entityKey) == null)
            return false;
        else
            return true;
    }

    /**
     * @return a List of all the process definition ids of all processes models for which configurations have been set.
     */
    public List<String> getArchivedProcesses() {
        return this.configuredProcesses;
    }

    /**
     * Updates the process definition id of a configuration that has been set.
     * 
     * @param oldProcessDefinitionId
     *            the process definition id under which the configuration was previously stored.
     * @param newProcessDefinitionId
     *            the new process definition id under which the configuration is to be stored from now on.
     */
    public void updateProcessDefinitionId(String oldProcessDefinitionId, String newProcessDefinitionId) {
        if (this.configuredProcesses.contains(oldProcessDefinitionId)) {
            this.configuredProcesses.remove(oldProcessDefinitionId);
        }
        this.configuredProcesses.add(newProcessDefinitionId);
        if (this.processConfigurationMap.get(oldProcessDefinitionId) != null) {
            Map<String, Boolean> configuration = this.processConfigurationMap.remove(oldProcessDefinitionId);
            this.processConfigurationMap.put(newProcessDefinitionId, configuration);
        }
    }
}