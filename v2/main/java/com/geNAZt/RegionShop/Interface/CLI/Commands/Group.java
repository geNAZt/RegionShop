package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.ItemStorageHolder;
import com.geNAZt.RegionShop.Database.Table.Chest;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import org.bukkit.command.CommandSender;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 07.10.13
 */
public class Group implements CLICommand {
    @Command(command="shop admin group", arguments=2, helpKey="Command_Group_HelpText", helpPage="admin", permission="rs.command.admin.group")
    public static void setgroup(CommandSender sender, String[] args) {
        String id = args[0];
        String group = args[1];

        //Check if group is there
        if(ConfigManager.main.getGroup(group) == null) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_NotFound);
            return;
        }

        //Check if id is correct
        if(!id.contains(":")) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_InvalidID);
            return;
        }

        String[] splitted = id.split(":");
        if(!splitted[0].equals("c") && !splitted[0].equals("r")) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_InvalidID);
            return;
        }

        Integer intID;
        try {
            intID = Integer.parseInt(splitted[1]);
        } catch(NumberFormatException e) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_InvalidID);
            return;
        }

        ItemStorageHolder itemStorageHolder = null;
        switch(splitted[0].charAt(0)) {
            case 'c':
                itemStorageHolder = Database.getServer().find(Chest.class).where().eq("id", intID).findUnique();
                break;

            case 'r':
                itemStorageHolder = Database.getServer().find(Region.class).where().eq("id", intID).findUnique();
                break;
        }

        if(itemStorageHolder == null) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_IDNotFound);
            return;
        }

        itemStorageHolder.getItemStorage().setSetting(group);
        Database.getServer().update(itemStorageHolder.getItemStorage());
        sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_Success);

    }
}
