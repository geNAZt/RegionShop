package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.Config.Files.Expert;
import com.geNAZt.RegionShop.Config.Files.Language;
import com.geNAZt.RegionShop.Config.Files.Main;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.FileReader;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class ConfigManager {
    public static Main main;
    public static Expert expert;
    public static Language language;

    public static void init(RegionShopPlugin plugin) {
        Yaml y = new Yaml(new CustomClassLoaderConstructor(RegionShopPlugin.class.getClassLoader()));

        try {
            expert = new Expert(plugin);
            expert.init();

            language = new Language(plugin);
            language.init();

            main = new Main(plugin);
            main.init();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
