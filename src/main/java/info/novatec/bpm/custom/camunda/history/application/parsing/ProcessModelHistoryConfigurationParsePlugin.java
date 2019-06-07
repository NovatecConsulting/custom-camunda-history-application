package info.novatec.bpm.custom.camunda.history.application.parsing;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.persistence.deploy.Deployer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ProcessEnginePlugin that introduces the {@link ProcessModelHistoryConfigurationParseListener} and the
 * {@link ProcessModelHistoryConfigurationDefinitionIdMatcher} to the Camunda Engine.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class ProcessModelHistoryConfigurationParsePlugin extends AbstractProcessEnginePlugin {

    @Autowired
    private ProcessModelHistoryConfigurationParseListener processModelHistoryConfigurationParseListener;

    @Autowired
    private ProcessModelHistoryConfigurationDefinitionIdMatcher processModelHistoryConfigurationDefinitionIdMatcher;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

        List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPreBPMNParseListeners();
        if (preParseListeners == null) {
            // if no custom preParseListener exist, create new list
            preParseListeners = new ArrayList<BpmnParseListener>();
            processEngineConfiguration.setCustomPreBPMNParseListeners(preParseListeners);
        }
        preParseListeners.add(this.processModelHistoryConfigurationParseListener);

        List<Deployer> postDeployers = processEngineConfiguration.getCustomPostDeployers();
        if (postDeployers == null) {
            // if no custom preParseListener exist, create new list
            postDeployers = new ArrayList<Deployer>();
            processEngineConfiguration.setCustomPostDeployers(postDeployers);
        }
        postDeployers.add(this.processModelHistoryConfigurationDefinitionIdMatcher);
    }
}