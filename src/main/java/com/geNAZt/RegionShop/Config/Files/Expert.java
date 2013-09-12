package com.geNAZt.RegionShop.Config.Files;

import com.geNAZt.RegionShop.Config.Config;
import com.geNAZt.RegionShop.Config.Sub.Location;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Expert extends Config {
    public Expert(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "expert.yml");
        CONFIG_HEADER = new String[]{
            "You only should alter this config file if you know what you do",
            "If you change something here the whole Server could be messed up after it",
            "You will find an exact documentation about all Config Settings at https://github.com/geNAZt/RegionShop/wiki"
        };

        //Set zero locations for all worlds
        List<World> worlds = Bukkit.getWorlds();
        for(World world : worlds) {
            this.Shop_Teleport.put(world.getName(), new Location());
        }
    }

    public Integer DB_maxConnections = 3;
    public Integer Timer_DetectWGChanges = 5*20;
    public Integer Tasks_AsyncDatabaseWriters = 1;
    public String Misc_regexPattern = "regionshop";
    public HashMap<String, Location> Shop_Teleport = new HashMap<String, Location>();
}
