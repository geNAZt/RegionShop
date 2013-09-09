package com.geNAZt.RegionShop.Config.Files;

import com.geNAZt.RegionShop.Config.Config;
import com.geNAZt.RegionShop.Config.Group;
import com.geNAZt.RegionShop.Config.GroupRentMode;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Main extends Config {
    public Main(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "main.yml");
        CONFIG_HEADER = new String[]{
            "This file holds the Settings that should be changed.",
            "For the documentation about the Config Settings visit https://github.com/geNAZt/RegionShop/wiki"
        };

        //Set the default group
        this.Group_Groups.add(new Group());

        //Enable all worlds
        List<World> worlds = Bukkit.getWorlds();
        for(World world : worlds) {
            this.World_enabledWorlds.add(world.getName());
        }
    }

    public String DB_url = "jdbc:sqlite:{DIR}RegionShop.db";
    public String DB_username = "walrus";
    public String DB_password = "bukkit";
    public String DB_driver = "org.sqlite.JDBC";
    public String Chat_prefix = "[RS] ";
    public ArrayList<String> World_enabledWorlds = new ArrayList<String>();
    public String Group_defaultGroup = "Default";
    public Boolean Group_use = true;
    public Boolean Group_calcRent = true;
    public Integer Group_rentInterval = 7*24*60*60;
    public GroupRentMode Group_rentMode = GroupRentMode.SPLIT_OWNERS;
    public ArrayList<Group> Group_Groups = new ArrayList<Group>();

    //Get a group by its name
    public Group getGroup(String name) {
        for(Group group : Group_Groups) {
            if(group.Name.equals(name)) {
                return group;
            }
        }

        return null;
    }
}
