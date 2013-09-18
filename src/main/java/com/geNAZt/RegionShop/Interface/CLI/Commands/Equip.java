package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.Drop;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.Logger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Equip implements CLICommand {
    @Command(command="shop equip", arguments=0, helpKey="Command_Equip_HelpText", helpPage="owner", permission="rs.command.equip")
    public static void equip(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            if (Drop.has(player)) {
                Region region = Drop.get(player);
                Drop.remove(player);

                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_Disabled.replace("%shop", region.getName()));
                return;
            } else {
                java.util.List<Region> regionList = Database.getServer().find(Region.class).
                        where().
                            eq("owners.name", player.getName().toLowerCase()).
                        findList();

                if (regionList.size() == 0) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_NoShopFound);
                    return;
                }

                if (regionList.size() > 1) {
                    for(String headerLine : ConfigManager.language.Command_Equip_MultipleShops) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + headerLine);
                    }

                    for(Region region : regionList) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_ShopName.replace("%name", region.getName()));
                    }

                    return;
                } else {
                    Region region = regionList.get(0);

                    Logger.info("Player " + player.getDisplayName() + " has toggled " + region.getId());

                    if(Drop.has(player)) {
                        Drop.remove(player);
                    }

                    Drop.put(player, region);

                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_Enabled.replace("%name", region.getName()));

                    return;
                }
            }
        }

        //Region warp
        Region region = Database.getServer().find(Region.class).where().eq("name", StringUtils.join(args, " ")).findUnique();

        if (region != null) {
            //Check if User is owner in this region
            java.util.List<com.geNAZt.RegionShop.Database.Table.Player> playerList = region.getOwners();
            boolean isOwner = false;

            for(com.geNAZt.RegionShop.Database.Table.Player player1 : playerList) {
                if(player1.getName().equals(player.getName().toLowerCase())) {
                    isOwner = true;
                }
            }

            if (!isOwner) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_NotOwner);
                return;
            }

            Logger.info("Player " + player.getName() + " has toggled " + region.getName());

            if(Drop.has(player)) {
                Drop.remove(player);
            }

            Drop.put(player, region);
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_Selected.replace("%name", StringUtils.join(args, " ")));

            return;
        }

        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_ShopNotFound);
    }
}
