package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Config.InvalidConfigurationException;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.09.13
 */
//This Shop Command is used to let the player teleport to the global shop teleportion point
public class Shop implements CLICommand {
    @Command(command="shop admin setteleport", arguments=0, permission="rs.command.admin.setteleport", helpKey="Command_Admin_SetTeleport_HelpText", helpPage="admin")
    public static void settp(CommandSender sender, String[] args) {
        //This command is not enabled for console
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        //Cast to player
        Player player = (Player) sender;

        //Get the postition of the Player
        Location position = player.getLocation();
        World world = player.getWorld();

        //Check if World is enabled for shops
        if(!ConfigManager.main.World_enabledWorlds.contains(world.getName())) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Admin_SetTeleport_WorldNotEnabled.replace("%world", world.getName()));
            return;
        }

        //Create a new Location SUbConfig element
        com.geNAZt.RegionShop.Config.Sub.Location location = new com.geNAZt.RegionShop.Config.Sub.Location();
        location.x = position.getX();
        location.y = position.getY();
        location.z = position.getZ();
        location.yaw = position.getYaw();
        location.pitch = position.getPitch();

        //Set it in the new Config
        ConfigManager.expert.Shop_Teleport.put(world.getName(), location);

        //Save the changed config
        try {
            ConfigManager.expert.save();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        //Tell the Player we have done it !
        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Admin_SetTeleport_Success.replace("%world", world.getName()));
    }

    @Command(command="shop", arguments=0, permission="rs.command.shop", helpKey="Command_Shop_HelpText", helpPage="consumer")
    public static void tp(CommandSender sender, String[] args) {
        //This command is not enabled for console
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        //Cast to player
        Player player = (Player) sender;

        //Get the world the player is in
        World world = player.getWorld();

        //Check if World is enabled for shops
        if(!ConfigManager.main.World_enabledWorlds.contains(world.getName())) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Shop_NotEnabled);
            return;
        }

        //Get the teleportation point
        com.geNAZt.RegionShop.Config.Sub.Location location = ConfigManager.expert.Shop_Teleport.get(world.getName());

        //Check if point is valid
        if(location == null || (location.x == 0.0 && location.y == 0.0 && location.z == 0.0 && location.pitch == 0.0F && location.yaw == 0.0F)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Shop_NoTPPoint);
            return;
        }

        //Teleport the Player
        Location location1 = new Location(world, location.x, location.y, location.z, location.yaw, location.pitch);
        player.teleport(location1);
    }
}