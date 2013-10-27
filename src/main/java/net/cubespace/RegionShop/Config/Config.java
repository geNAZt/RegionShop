package net.cubespace.RegionShop.Config;

import net.cubespace.RegionShop.Util.Logger;
import org.apache.commons.lang.Validate;

import java.io.*;

/**
 * This abstract class is the part of save/load handling for ConfigObjects
 *
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last modified 26.10.2013 16:54
 */
public abstract class Config extends ConfigObject {
    protected transient File CONFIG_FILE = null;
    protected transient String[] CONFIG_HEADER = null;
    private YamlConfiguration yamlConfiguration = null;

    /**
     * This function loads a specific YAML into the ConfigObject. Please only use this function is you are building
     * some sort of Database in YAML and not on regular basis, it can cause some Problems if you do so :D
     *
     * @param file The file which should be loaded into the ConfigObject
     * @return Config
     */
    public Config load(File file) {
        /* Check if File is valid, store it inside the Object and reload the Object */
        Validate.notNull(file, "File can not be null");
        Validate.isTrue(!file.exists(), "File does not exist");

        CONFIG_FILE = file;

        return reload();
    }

    /**
     * This function reloads the File and parses it into the given ConfigObject
     *
     * @return Config
     */
    public Config reload() {
        /* Check if CONFIG_FILE is set */
        Validate.notNull(CONFIG_FILE, "File can not be null");

        /* Load the YAML and let the ConfigObject parse it */
        yamlConfiguration = YamlConfiguration.loadConfiguration(CONFIG_FILE);

        /* Tell the ConfigObject we loaded */
        try {
            onLoad(yamlConfiguration);
            yamlConfiguration.save(CONFIG_FILE);
        } catch (Exception e) {
            Logger.error("Could not load/save YAML Config", e);
        }

        return this;
    }

    /**
     * Save the changes made to the ConfigObject into a specific File. Please only use that if you want to build a YAML
     * Database. If you use this in normal Config Files it can cause problems. Please take care that the CONFIG_FILE in
     * the ConfigObject gets changed, so if you save() or reload() it tries to save/reload from the File you gave here.
     *
     * @param file The file where the ConfigObject should be saved to
     * @return Config
     */
    public Config save(File file) {
        /* Check if File is valid, store it inside the Object and save the Object */
        Validate.notNull(file, "File can not be null");

        CONFIG_FILE = file;

        return save();
    }

    /**
     * Saves the current ConfigObject. It does not reload the Config before saving. So if you save, changes you made
     * on the Filesystems will be overwritten. If you want to get new Contents from the Filesystem use reload()
     *
     * @return Config
     */
    public Config save() {
        /* Be sure that a File has been set */
        Validate.notNull(CONFIG_FILE, "File can not be null");

        /* If the Config File does not exists create it */
        if(!CONFIG_FILE.exists()) {
            try {
                /* Create parent Directories if possible */
                if(CONFIG_FILE.getParentFile() != null) CONFIG_FILE.getParentFile().mkdirs();

                /* Create a new empty File */
                CONFIG_FILE.createNewFile();

                /* Write the Header into it */
                if (CONFIG_HEADER != null) {
                    Writer newConfig = new BufferedWriter(new FileWriter(CONFIG_FILE));
                    for (String line : CONFIG_HEADER) {
                        newConfig.write("# " + line + "\n");
                    }

                    newConfig.close();
                }
            } catch (Exception e) {
                Logger.error("Could not create new Config File", e);
            }

            yamlConfiguration = new YamlConfiguration();
        }

        /* Tell the ConfigObject we want to save */
        try {
            onSave(yamlConfiguration);
            yamlConfiguration.save(CONFIG_FILE);
        } catch (Exception e) {
            Logger.error("Could not save the Config", e);
        }

        return this;
    }

    /**
     * Let the ConfigObject be backed by the File given into the parameters. It will be loaded if existing, if not
     * the ConfigObject will be saved into it with its default values
     *
     * @param file The file the ConfigObject should use
     * @return Config
     */
    public Config init(File file) {
        /* Check if File is valid, store it inside the Object and init the Object */
        Validate.notNull(file, "File can not be null");

        CONFIG_FILE = file;

        return init();
    }

    /**
     * If you have given an internal ressource to the ConfigObject you dont need to give a File into the init. You can
     * use this function to load the Config from the internal Ressource given
     *
     * @return Config
     */
    public Config init() {
        /* Check if File is valid, store it inside the Object and init the Object */
        Validate.notNull(CONFIG_FILE, "File can not be null");

        if (CONFIG_FILE.exists()) return reload();
        else return save();
    }
}