package info.novatec.bpm.custom.camunda.history.application.configurations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import info.novatec.bpm.custom.camunda.history.application.configurations.ProcessHistoryConfiguration;

/**
 * Testclass for {@link ProcessHistoryConfiguration}.
 * 
 * @author Ben Fuernrohr
 */
public class TestProcessHistoryArchiveConfiguration {

    /** Subject under Test */
    private ProcessHistoryConfiguration processHistoryConfiguration = new ProcessHistoryConfiguration();

    @Test
    public void adding_config_to_process_archive_should_allow_those_to_be_retreived() {
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition", "process-instance",
            true);
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition", "variable-instance",
            false);
        this.processHistoryConfiguration.addConfigToProcessArchive("mySecondProcessDefinition",
            "process-instance", false);
        this.processHistoryConfiguration.addConfigToProcessArchive("mySecondProcessDefinition",
            "variable-instance",
            true);

        assertTrue(
            this.processHistoryConfiguration.isConfigurationSetForProcessDefinitionId("myProcessDefinition",
                "process-instance"));
        assertTrue(
            this.processHistoryConfiguration.isConfigurationSetForProcessDefinitionId("myProcessDefinition",
                "variable-instance"));
        assertTrue(
            this.processHistoryConfiguration
                .isConfigurationSetForProcessDefinitionId("mySecondProcessDefinition", "process-instance"));
        assertTrue(
            this.processHistoryConfiguration
                .isConfigurationSetForProcessDefinitionId("mySecondProcessDefinition", "variable-instance"));

        assertTrue(this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
            "myProcessDefinition",
            "process-instance"));
        assertFalse(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myProcessDefinition",
                "variable-instance"));
        assertFalse(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "mySecondProcessDefinition",
                "process-instance"));
        assertTrue(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "mySecondProcessDefinition",
                "variable-instance"));
    }

    @Test
    public void retreiving_nonexisting_configuration_should_return_null() {
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition", "process-instance",
            true);
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition", "variable-instance",
            false);

        assertFalse(
            this.processHistoryConfiguration.isConfigurationSetForProcessDefinitionId("myOtherProcessDefinition",
                "process-instance"));
        assertEquals(null,
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myOtherProcessDefinition",
                "process-instance"));

        assertEquals(null,
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myProcessDefinition",
                "incident"));
    }

    @Test
    public void configurations_should_be_overwritten_correctly() {
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition", "process-instance",
            true);
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition", "variable-instance",
            false);

        assertTrue(
            this.processHistoryConfiguration.isConfigurationSetForProcessDefinitionId("myProcessDefinition",
                "process-instance"));

        assertTrue(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myProcessDefinition",
                "process-instance"));
        assertFalse(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myProcessDefinition",
                "variable-instance"));

        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition",
            "process-instance", false);
        this.processHistoryConfiguration.addConfigToProcessArchive("myProcessDefinition",
            "variable-instance",
            true);

        assertTrue(
            this.processHistoryConfiguration.isConfigurationSetForProcessDefinitionId("myProcessDefinition",
                "process-instance"));

        assertFalse(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myProcessDefinition",
                "process-instance"));
        assertTrue(
            this.processHistoryConfiguration.getConfigurationForProcessDefinitionIdAndEntityKey(
                "myProcessDefinition",
                "variable-instance"));
    }
}