package net.cubespace.RegionShop.Config.Files;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.Config;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 30.10.13 17:20
 */
public class Main extends Config {
    public Main() {
        CONFIG_FILE = new File(Plugin.getInstance().getDataFolder() + File.separator + "config" + File.separator + "main.yml");
        CONFIG_HEADER = new String[]{
                "This file holds the Settings that should be changed.",
                "For the documentation about the Config Settings visit https://github.com/geNAZt/RegionShop/wiki"
        };

        //Enable all worlds
        List<World> worlds = Bukkit.getWorlds();
        for(World world : worlds) {
            this.World_enabledWorlds.add(world.getName());
        }
    }

    public Boolean Updater = true;
    public String DB_url = "jdbc:h2:{DIR}RegionShop.db.h2";
    public String DB_username = "walrus";
    public String DB_password = "bukkit";
    public String Chat_prefix = "[RS] ";
    public ArrayList<String> World_enabledWorlds = new ArrayList<String>();

}
