package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.ItemStorageHolder;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class Group implements CLICommand {
    @Command(command="shop admin group", arguments=2, helpKey="Command_Group_HelpText", helpPage="admin", permission="rs.command.admin.group")
    public static void setgroup(CommandSender sender, String[] args) {
        String id = args[0];
        String group = args[1];

        //Check if group is there
        if(ConfigManager.groups.getGroup(group) == null) {
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

        try {
            ItemStorageHolder itemStorageHolder = null;
            switch(splitted[0].charAt(0)) {
                case 'c':
                    itemStorageHolder = Database.getDAO(Chest.class).queryBuilder().where().eq("id", intID).queryForFirst();
                    break;

                case 'r':
                    itemStorageHolder = Database.getDAO(Region.class).queryBuilder().where().eq("id", intID).queryForFirst();
                    break;
            }

            if(itemStorageHolder == null) {
                sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_IDNotFound);
                return;
            }

            itemStorageHolder.getItemStorage().setSetting(group);
            Database.getDAO(ItemStorage.class).update(itemStorageHolder.getItemStorage());
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Group_Success);
        } catch (SQLException e) {
            Logger.error("Could not update Group", e);
        }
    }
}
