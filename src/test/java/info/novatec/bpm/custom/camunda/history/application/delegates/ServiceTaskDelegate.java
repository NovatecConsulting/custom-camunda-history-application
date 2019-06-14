package info.novatec.bpm.custom.camunda.history.application.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.Incident;
import org.springframework.stereotype.Component;

/**
 * Dummy implementation of service task. Does nothing but set some variables, create and resolve an incident.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class ServiceTaskDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("boolean1", true);
        execution.setVariable("string1", "Hello!");
        execution.setVariable("integer1", 1);

        Incident incident = execution.getProcessEngineServices()
            .getRuntimeService()
            .createIncident("testIncident", execution.getId(), "configuration");
        execution.getProcessEngineServices().getRuntimeService().resolveIncident(incident.getId());
    }
}