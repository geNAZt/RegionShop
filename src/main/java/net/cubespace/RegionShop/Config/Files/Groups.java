package net.cubespace.RegionShop.Config.Files;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.Config;
import net.cubespace.RegionShop.Config.Sub.Group;
import net.cubespace.RegionShop.Config.Sub.GroupRentMode;

import java.io.File;
import java.util.ArrayList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 30.10.13 17:22
 */
public class Groups extends Config {
    public Groups() {
        CONFIG_FILE = new File(Plugin.getInstance().getDataFolder() + File.separator + "config" + File.separator + "groups.yml");
        CONFIG_HEADER = new String[]{
                "This File holds all Groups which can be used in RegionShop",
                "For the documentation about the Config Settings visit https://github.com/geNAZt/RegionShop/wiki"
        };

        //Set the default group
        this.Group_Groups.add(new Group());

        Group normalChest = new Group();
        normalChest.Storage = 27*64;
        normalChest.Name = "Normal Chest";

        this.Group_Groups.add(normalChest);

        Group doubleChest = new Group();
        doubleChest.Storage = 54*64;
        doubleChest.Name = "Double Chest";

        this.Group_Groups.add(doubleChest);
    }

    public String Group_defaultGroup = "Default";
    public String Group_defaultChestShop = "Normal Chest";
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
