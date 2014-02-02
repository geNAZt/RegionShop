package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter.DamageFilter;
import net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter.EnchFilter;
import net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter.PriceFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Filter implements CLICommand {
    @Command(command="shop filter", arguments=1, helpKey="Command_Filter_HelpText", helpPage="consumer", permission="rs.command.filter")
    public static void filter(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        if (net.cubespace.RegionShop.Data.Storage.Search.has(player)) {
            //Parse all filters
            ArrayList<net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter.Filter> filters = new ArrayList<net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter.Filter>();

            for(String filter:args) {
                if(!filter.contains(":")) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Filter_InvalidSyntax.replace("%filter", filter));
                    return;
                }

                String[] parts = filter.split(":");
                if(parts[0].equals("p")) {
                    PriceFilter filterObj = new PriceFilter();
                    String error = filterObj.parse(parts[1]);

                    if(error != null) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + error);
                        return;
                    }

                    filters.add(filterObj);
                } else if(parts[0].equals("d")) {
                    DamageFilter filterObj = new DamageFilter();
                    String error = filterObj.parse(parts[1]);

                    if(error != null) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + error);
                        return;
                    }

                    filters.add(filterObj);
                } else if(parts[0].equals("e")) {
                    EnchFilter filterObj = new EnchFilter();
                    String error = filterObj.parse(parts[1]);

                    if(error != null) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + error);
                        return;
                    }

                    filters.add(filterObj);
                }
            }

            ConcurrentHashMap<Items, ItemStack> result = net.cubespace.RegionShop.Data.Storage.Search.getSearchResult(player);
            ConcurrentHashMap<Items, ItemStack> newResult = new ConcurrentHashMap<Items, ItemStack>();

            for(Map.Entry<Items, ItemStack> entry : result.entrySet()) {
                boolean in = true;

                for(net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter.Filter fil : filters) {
                    if(!fil.checkItem(entry.getKey(), entry.getValue())) {
                        in = false;
                        break;
                    }
                }

                if(in) newResult.put(entry.getKey(), entry.getValue());
            }

            String query = net.cubespace.RegionShop.Data.Storage.Search.getSearchQuery(player);
            net.cubespace.RegionShop.Data.Storage.Search.put(player, query, newResult);

            Result.printResultPage(player, query, newResult, 1);
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Filter_NoResults);
        }
    }
}
