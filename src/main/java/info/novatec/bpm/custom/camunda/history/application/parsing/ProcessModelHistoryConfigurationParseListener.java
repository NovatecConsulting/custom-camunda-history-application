package info.novatec.bpm.custom.camunda.history.application.parsing;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;

/**
 * Parse-Listener to extract extension elements from a process definition and store history specific properties in a
 * {@link ProcessHistoryConfiguration}.
 * 
 * @author Ben Fuernrohr
 */
@Component
public class ProcessModelHistoryConfigurationParseListener extends AbstractBpmnParseListener {

    /** List of valid history extension properties for BPMN process models */
    private final List<String> historyPropertyList = Arrays
        .asList(new String[] {"process-instance", "activity-instance", "task-instance", "variable-instance", "incident",
            "case-instance", "case-activity-instance", "decision", "external-task", "user-operation-log"});

    @Autowired
    private ProcessHistoryConfiguration processHistoryConfiguration;

    @Override
    public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
        // get the <extensionElements ...> element
        Element extensionElement = processElement.element("extensionElements");
        if (extensionElement != null) {
            // get the <camunda:properties ...> element
            Element propertiesElement = extensionElement.element("properties");
            if (propertiesElement != null) {
                // get list of <camunda:property ...> elements
                List<Element> propertyList = propertiesElement.elements("property");
                for (Element property : propertyList) {
                    // get the name and the value of the extension property element and add them to the configuration
                    if (this.historyPropertyList.contains(property.attribute("name"))) {
                        this.processHistoryConfiguration.addConfigToProcessArchive(processDefinition.getKey(),
                            property.attribute("name"), Boolean.parseBoolean(property.attribute("value")));
                        // create an entry for process-instance-update if one was set for process-instance, because
                        // those are treated seperately by camunda for some reason
                        if (property.attribute("name") == "process-instance")
                            this.processHistoryConfiguration.addConfigToProcessArchive(
                                processDefinition.getKey(),
                                "process-instance-update", Boolean.parseBoolean(property.attribute("value")));
                    }
                }
            }
        }
    }
}