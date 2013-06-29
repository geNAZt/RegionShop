package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Filter.DamageFilter;
import com.geNAZt.RegionShop.Filter.EnchFilter;
import com.geNAZt.RegionShop.Filter.Filter;
import com.geNAZt.RegionShop.Filter.PriceFilter;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Storages.SearchStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public class ShopFilter extends ShopCommand {
    private final Plugin plugin;

    public ShopFilter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop filter " + ChatColor.RED + "filter" + ChatColor.RESET + ": Filter the result"};
    }

    @Override
    public String getCommand() {
        return "filter";
    }

    @Override
    public String getPermissionNode() {
        return "rs.search";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (SearchStorage.hasPlayer(player)) {
            //Parse all filters
            ArrayList<Filter> filters = new ArrayList<Filter>();

            for(String filter:args) {
                if(!filter.contains(":")) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Error in Filtersyrtax. " + filter + " has no :");
                    return;
                }

                String[] parts = filter.split(":");
                if(parts[0].equals("p")) {
                    PriceFilter filterObj = new PriceFilter();
                    String error = filterObj.parse(parts[1]);

                    if(error != null) {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + error);
                        return;
                    }

                    filters.add(filterObj);
                } else if(parts[0].equals("d")) {
                    DamageFilter filterObj = new DamageFilter();
                    String error = filterObj.parse(parts[1]);

                    if(error != null) {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + error);
                        return;
                    }

                    filters.add(filterObj);
                } else if(parts[0].equals("e")) {
                    EnchFilter filterObj = new EnchFilter();
                    String error = filterObj.parse(parts[1]);

                    if(error != null) {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + error);
                        return;
                    }

                    filters.add(filterObj);
                }
            }

            ConcurrentHashMap<ShopItems, ItemStack> result = SearchStorage.getSearchResult(player);
            ConcurrentHashMap<ShopItems, ItemStack> newResult = new ConcurrentHashMap<ShopItems, ItemStack>();

            for(Map.Entry<ShopItems, ItemStack> entry : result.entrySet()) {
                boolean in = true;

                for(Filter fil : filters) {
                    if(!fil.checkItem(entry.getKey(), entry.getValue())) {
                        in = false;
                        break;
                    }
                }

                if(in) newResult.put(entry.getKey(), entry.getValue());
            }

            String query = SearchStorage.getSearchQuery(player);
            SearchStorage.putSearchResults(player, query, newResult);

            ShopResult.printResultPage(player, query, newResult, 1);
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You have no results");
        }
    }
}
