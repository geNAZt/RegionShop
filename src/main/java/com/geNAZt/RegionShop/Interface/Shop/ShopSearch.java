package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Storages.SearchStorage;

import org.apache.commons.lang.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopSearch extends ShopCommand {
    private final Plugin plugin;

    public ShopSearch(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "search";
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
        String search = StringUtils.join(args, "_");

        List<ShopItems> items = plugin.getDatabase().find(ShopItems.class).findList();
        if(items != null) {
            Pattern r = Pattern.compile("(.*)" + search + "(.*)");
            HashMap<ShopItems, ItemStack> result = new HashMap<ShopItems, ItemStack>();

            for(ShopItems item : items) {
                ItemStack iStack = ItemConverter.fromDBItem(item);

                String searchString = ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString());

                Matcher m = r.matcher(searchString);

                if (m.matches() && ((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0)) {
                    result.put(item, iStack);
                }
            }

            if(!result.isEmpty()) {
                if(SearchStorage.hasPlayer(player)) {
                    SearchStorage.removeAllPlayer(player);
                }

                SearchStorage.putSearchResults(player, search, result);
                ShopResult.printResultPage(player, search, result, 1);

            } else {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No items found for your search");
            }
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No items in shops");
        }
    }
}
