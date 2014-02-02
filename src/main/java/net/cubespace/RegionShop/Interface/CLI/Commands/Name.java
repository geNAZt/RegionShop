package net.cubespace.RegionShop.Interface.CLI.Commands;

import com.google.common.base.CharMatcher;
import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsRegion;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.Logger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

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
            ForeignCollection<PlayerOwnsRegion> playerList = InRegion.get(player).getOwners();
            boolean isOwner = false;

            for(PlayerOwnsRegion player1 : playerList) {
                if(player1.getPlayer().getName().equals(player.getName().toLowerCase())) {
                    isOwner = true;
                }
            }

            if(player.hasPermission("rs.bypass.name")) {
                isOwner = true;
            }

            if (isOwner) {
                Region region1 = null;
                try {
                    region1 = Database.getDAO(Region.class).queryBuilder().where().eq("lcName", name.toLowerCase()).queryForFirst();
                } catch (SQLException e) {
                    Logger.error("Could not get Region", e);
                }

                if(region1 != null) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_AlreadyTaken);
                    return;
                }

                if(ConfigManager.main.Expert_OnlyASCII) {
                    if (!CharMatcher.ASCII.matchesAllOf(name)) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_OnlyASCII);
                        return;
                    }
                }

                if(name.length() > ConfigManager.main.Expert_MaxShopName) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_TooLong.replace("%amount", ConfigManager.main.Expert_MaxShopName.toString()));
                    return;
                }

                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Name_Success);

                region.setName(name);
                region.setLcName(name.toLowerCase());
                try {
                    Database.getDAO(Region.class).update(region);
                } catch (SQLException e) {
                    Logger.warn("Could not update the Name of the Region", e);
                }

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
