package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.Logger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.sql.SQLException;

public class Shop implements CLICommand {
    @Command(command="shop admin setteleport", arguments=0, permission="rs.command.admin.setteleport", helpKey="Command_Admin_SetTeleport_HelpText", helpPage="admin")
    public static void settp(CommandSender sender, String[] args) {
        //This command is not enabled for console
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
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
        net.cubespace.RegionShop.Config.Files.Sub.Location location = new net.cubespace.RegionShop.Config.Files.Sub.Location();
        location.x = position.getX();
        location.y = position.getY();
        location.z = position.getZ();
        location.yaw = position.getYaw();
        location.pitch = position.getPitch();

        //Set it in the new Config
        ConfigManager.main.Shop_Teleport.put(world.getName(), location);

        //Save the changed config
        ConfigManager.main.save();

        //Tell the Player we have done it !
        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Admin_SetTeleport_Success.replace("%world", world.getName()));
    }

    @Command(command="shop", arguments=0, permission="rs.command.shop", helpKey="Command_Shop_HelpText", helpPage="consumer")
    public static void tp(CommandSender sender, String[] args) {
        //This command is not enabled for console
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        //Cast to player
        Player player = (Player) sender;

        //Check if we can teleport to a Shop
        if(args.length > 0 && player.hasPermission("rs.command.shop.teleporttoshop")) {
            String name = StringUtils.join(args, " ");
            Integer id;

            try {
                id = Integer.parseInt(name);

                Chest chest = Database.getDAO(Chest.class).queryBuilder().where().eq("id", id).queryForFirst();

                if (chest == null) {
                    sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_NotKnown);

                    return;
                }

                Location location = new Location(Bukkit.getWorld(chest.getWorld()), chest.getChestX(), chest.getChestY(), chest.getChestZ());

                player.teleport(location);
            } catch(NumberFormatException e) {
                Region region = null;
                try {
                    region = Database.getDAO(Region.class).queryBuilder().
                            where().
                                eq("lcName", name.toLowerCase()).
                            queryForFirst();
                } catch (SQLException e1) {
                    Logger.error("Could not get Region", e1);
                }

                if(region == null) {
                    sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_NotKnown);

                    return;
                }

                Vector min = new Vector(region.getMinX(), region.getMinY(), region.getMinZ());
                Vector max = new Vector(region.getMaxX(), region.getMaxY(), region.getMaxZ());

                Vector mid = min.getMidpoint(max);

                Location location = new Location(Bukkit.getWorld(region.getWorld()), mid.getBlockX(), mid.getBlockY(), mid.getBlockZ());

                player.teleport(location);
            } catch (SQLException e) {
                Logger.error("Could not get Chest", e);
            }

            return;
        }

        //Get the world the player is in
        World world = player.getWorld();

        //Check if World is enabled for shops
        if(!ConfigManager.main.World_enabledWorlds.contains(world.getName())) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Shop_NotEnabled);
            return;
        }

        //Get the teleportation point
        net.cubespace.RegionShop.Config.Files.Sub.Location location = ConfigManager.main.Shop_Teleport.get(world.getName());

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
