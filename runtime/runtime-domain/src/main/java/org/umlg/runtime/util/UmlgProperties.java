package org.umlg.runtime.util;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.io.File;

/**
 * Date: 2013/01/02
 * Time: 3:08 PM
 */
public class UmlgProperties {

    public static UmlgProperties INSTANCE = new UmlgProperties();
    private CompositeConfiguration properties;
    private CompositeConfiguration internalProperties;

    private UmlgProperties() {
        try {
            this.properties = new CompositeConfiguration();
            PropertiesConfiguration pc = new PropertiesConfiguration("umlg.env.properties");
            this.properties.addConfiguration(pc);
        } catch (Exception e) {
            throw new RuntimeException("Expecting \"umlg.env.properties\" file on the classpath with ");
        }
        try {
            this.internalProperties = new CompositeConfiguration();
            PropertiesConfiguration internalPc = new PropertiesConfiguration("umlg.internal.properties");
            this.internalProperties.addConfiguration(internalPc);
        } catch (Exception e) {
            throw new RuntimeException("Expecting \"umlg.internal.properties\" file on the classpath with ");
        }
        try {
            PropertiesConfiguration overrideProperties = null;
            if (isDistribution() && Thread.currentThread().getContextClassLoader().getResource("WEB-INF/web.xml") != null) {
                //own assembly
                File f = new File("../resources/" + this.properties.getProperty("umlg.model.file.name") + ".umlg.env.properties");
                overrideProperties = new PropertiesConfiguration(f.getAbsolutePath());
            } else if (isWebContainer()) {
                //tomcat or glasfish or jetty
                overrideProperties = new PropertiesConfiguration(this.properties.getProperty("umlg.model.file.name") + ".umlg.env.properties");
            }
            if (overrideProperties != null) {
                overrideProperties.setReloadingStrategy(new FileChangedReloadingStrategy());
                this.properties.addConfiguration(overrideProperties);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not find " + "../resources/" + this.properties.getProperty("model.name") + ".umlg.env.properties");
        }
    }

    public CompositeConfiguration getProperties() {
        return properties;
    }

    private boolean isDistribution() {
        return Boolean.valueOf(System.getProperty("UMLGServerDistribution", "false"));
    }

    private boolean isWebContainer() {
        return false;
    }

    public String getUmlgDbRootLocation() {
        return this.properties.getString("umlg.db.location", System.getProperty("java.io.tmpdir"));
    }

    public boolean getCreatedOnUpdatedOnInit() {
        return this.properties.getBoolean("createdon.updatedon.init", true);
    }

    public String getModelFileName() {
        return this.properties.getString("umlg.model.file.name");
    }

    public String getModelJavaName() {
        return this.internalProperties.getString("model.java.name");
    }

    public String getUmlgDbLocation() {
        return this.getUmlgDbRootLocation() + "/" + this.getModelFileName();
    }

    public boolean isStartAdminApplication() {
        return this.properties.getBoolean("start.admin.application", false);
    }

    public boolean isClearDbOnStartUp() {
        return this.properties.getBoolean("start.clear.db", false);
    }

    public boolean isCreateDefaultData() {
        return this.properties.getBoolean("create.default.data", false);
    }

    public String getDefaultDataLoaderClass() {
        return this.properties.getString("default.data.class");
    }

    public boolean isLoadUiResourcesFromFile() {
        return this.properties.getBoolean("umlg.ui.from.file", false);
    }

    public String getWebserverIp() {
        return this.properties.getString("webserver.ip", "127.0.0.1");
    }

    public int getWebserverPort() {
        return this.properties.getInteger("webserver.port", 8080);
    }

    public String[] getSqlgPropertiesLocation() {
        return this.properties.getStringArray("sqlg.properties.location");
    }
}
