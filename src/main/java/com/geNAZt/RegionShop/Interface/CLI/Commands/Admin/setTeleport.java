package com.geNAZt.RegionShop.Interface.CLI.Commands.Admin;

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
public class setTeleport implements CLICommand {
    @Override
    public String[] getHelp() {
        return new String[]{ConfigManager.language.Admin_SetTeleport_HelpText};
    }

    @Command(command="shop admin setteleport", arguments=0, permission="rs.command.admin.setteleport")
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

        //Create a new Location SUbConfig element
        com.geNAZt.RegionShop.Config.Sub.Location location = new com.geNAZt.RegionShop.Config.Sub.Location();
        location.x = position.getBlockX();
        location.y = position.getBlockY();
        location.z = position.getBlockZ();
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
    }
}
