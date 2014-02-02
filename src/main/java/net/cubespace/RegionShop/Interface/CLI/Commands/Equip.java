package net.cubespace.RegionShop.Interface.CLI.Commands;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.Drop;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.PlayerRepository;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsRegion;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.Logger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Equip implements CLICommand {
    @Command(command="shop equip", arguments=0, helpKey="Command_Equip_HelpText", helpPage="owner", permission="rs.command.equip")
    public static void equip(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
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
                net.cubespace.RegionShop.Database.Table.Player player1 = PlayerRepository.get(player);

                if (player1.getOwnsRegion().size() == 0) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_NoShopFound);
                    return;
                }

                if (player1.getOwnsRegion().size() > 1) {
                    for(String headerLine : ConfigManager.language.Command_Equip_MultipleShops) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + headerLine);
                    }

                    for(PlayerOwnsRegion region : player1.getOwnsRegion()) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_ShopName.replace("%name", region.getRegion().getName()));
                    }

                    return;
                } else {
                    PlayerOwnsRegion region = player1.getOwnsRegion().iterator().next();

                    Logger.info("Player " + player.getDisplayName() + " has toggled " + region.getRegion().getId());

                    if(Drop.has(player)) {
                        Drop.remove(player);
                    }

                    Drop.put(player, region.getRegion());

                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Equip_Enabled.replace("%name", region.getRegion().getName()));

                    return;
                }
            }
        }

        //Region warp
        Region region = null;
        try {
            region = Database.getDAO(Region.class).queryBuilder().where().eq("name", StringUtils.join(args, " ")).queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not get Region", e);
        }

        if (region != null) {
            //Check if User is owner in this region
            ForeignCollection<PlayerOwnsRegion> playerList = region.getOwners();
            boolean isOwner = false;

            for(PlayerOwnsRegion player1 : playerList) {
                if(player1.getPlayer().getName().equals(player.getName().toLowerCase())) {
                    isOwner = true;
                }
            }

            if(player.hasPermission("rs.bypass.equip")) {
                isOwner = true;
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
