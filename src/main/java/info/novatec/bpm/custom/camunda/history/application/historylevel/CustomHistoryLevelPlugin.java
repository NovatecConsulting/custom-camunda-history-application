package info.novatec.bpm.custom.camunda.history.application.historylevel;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ProcessEnginePlugin that introduces the {@link CustomHistoryLevel} to the Camunda Engine.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class CustomHistoryLevelPlugin extends AbstractProcessEnginePlugin {

    @Autowired
    private CustomHistoryLevel customHistoryLevel;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<HistoryLevel> customHistoryLevels = processEngineConfiguration.getCustomHistoryLevels();
        if (customHistoryLevels == null) {
            // if no custom history levels exists, create new list
            customHistoryLevels = new ArrayList<HistoryLevel>();
            processEngineConfiguration.setCustomHistoryLevels(customHistoryLevels);
        }
        customHistoryLevels.add(this.customHistoryLevel);
    }
}
