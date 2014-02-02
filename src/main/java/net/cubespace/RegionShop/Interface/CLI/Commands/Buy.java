package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Buy implements CLICommand {
    @Command(command="shop buy", arguments=1, helpKey="Command_Buy_HelpText", helpPage="consumer", permission="rs.command.buy")
    public static void buy(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        //Convert args
        Integer shopItemId, wishAmount = 1;

        try {
            if(args.length > 1) {
                wishAmount = Integer.parseInt(args[1]);
            }

            shopItemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_InvalidArguments);
            return;
        }

        //Is player in region ?
        if (!InRegion.has(player)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_NotInRegion);
            return;
        }

        Region region = InRegion.get(player);

        //Get the item
        Items item = null;
        try {
            item = Database.getDAO(Items.class).queryBuilder().
                    where().
                    eq("id", shopItemId).
                    queryForFirst();
        } catch (SQLException e) {
            Plugin.getInstance().getLogger().severe("Could not get the Item of the Database");
        }

        //No luck for you => no item found
        if (item == null) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_ItemNotFound);
            return;
        }

        net.cubespace.RegionShop.Core.Buy.buy(item, player, region, wishAmount);
    }
}
