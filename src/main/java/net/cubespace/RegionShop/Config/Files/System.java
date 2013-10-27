package net.cubespace.RegionShop.Config.Files;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.Config;

import java.io.File;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 13:46
 */
public class System extends Config {
    public System() {
        CONFIG_FILE = new File(Plugin.getInstance().getDataFolder().getAbsolutePath() + File.separator + "system.yml");
        CONFIG_HEADER = new String[]{
            "This file should never be edited by hand. It holds Informations about the internal Component Versions"
        };
    }

    public Integer Version_Database = 1;
    public Integer Version_DataValues = 1;
}
