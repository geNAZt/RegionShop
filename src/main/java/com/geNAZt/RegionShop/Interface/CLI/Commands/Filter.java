package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter.DamageFilter;
import com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter.EnchFilter;
import com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter.PriceFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public class Filter implements CLICommand {
    @Command(command="shop filter", arguments=1, helpKey="Command_Filter_HelpText", helpPage="consumer", permission="rs.command.filter")
    public static void filter(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        Player player = (Player) sender;

        if (com.geNAZt.RegionShop.Data.Storage.Search.has(player)) {
            //Parse all filters
            ArrayList<com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter.Filter> filters = new ArrayList<com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter.Filter>();

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

            ConcurrentHashMap<Items, ItemStack> result = com.geNAZt.RegionShop.Data.Storage.Search.getSearchResult(player);
            ConcurrentHashMap<Items, ItemStack> newResult = new ConcurrentHashMap<Items, ItemStack>();

            for(Map.Entry<Items, ItemStack> entry : result.entrySet()) {
                boolean in = true;

                for(com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter.Filter fil : filters) {
                    if(!fil.checkItem(entry.getKey(), entry.getValue())) {
                        in = false;
                        break;
                    }
                }

                if(in) newResult.put(entry.getKey(), entry.getValue());
            }

            String query = com.geNAZt.RegionShop.Data.Storage.Search.getSearchQuery(player);
            com.geNAZt.RegionShop.Data.Storage.Search.put(player, query, newResult);

            Result.printResultPage(player, query, newResult, 1);
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Filter_NoResults);
        }
    }
}
