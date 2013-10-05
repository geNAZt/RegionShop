package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Name implements CLICommand {
    @Command(command="shop name", arguments=1, permission="rs.command.name", helpKey="Command_Name_HelpText", helpPage="owner")
    public static void execute(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        String name = StringUtils.join(args, " ");

        if(InRegion.has(player)) {
            Region region = InRegion.get(player);

            //Check if User is owner in this region
            java.util.List<com.geNAZt.RegionShop.Database.Table.Player> playerList = InRegion.get(player).getOwners();
            boolean isOwner = false;

            for(com.geNAZt.RegionShop.Database.Table.Player player1 : playerList) {
                if(player1.getName().equals(player.getName().toLowerCase())) {
                    isOwner = true;
                }
            }

            if(player.hasPermission("rs.bypass.name")) {
                isOwner = true;
            }

            if (isOwner) {
                Region region1 = Database.getServer().find(Region.class).where().eq("lcname", name.toLowerCase()).findUnique();

                if(region1 != null) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_AlreadyTaken);
                    return;
                }

                if(ConfigManager.expert.OnlyASCII) {
                    if (!CharMatcher.ASCII.matchesAllOf(name)) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_OnlyASCII);
                        return;
                    }
                }

                if(name.length() > ConfigManager.expert.MaxShopName) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_TooLong.replace("%amount", ConfigManager.expert.MaxShopName.toString()));
                    return;
                }

                region.setName(name);
                region.setLcName(name.toLowerCase());
                Database.getServer().update(region);

                return;
            } else {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_NotOwner);
                return;
            }
        }

        //Nothing of all
        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_NotInRegion);
    }
}
