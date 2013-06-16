package com.geNAZt.RegionShop.Command.Shop;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;

import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Storages.SearchStorage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopSearch {
    private final RegionShopPlugin plugin;

    public ShopSearch(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(Player p, String search) {
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
                if(SearchStorage.hasPlayer(p)) {
                    SearchStorage.removeAllPlayer(p);
                }

                SearchStorage.putSearchResults(p, search, result);
                ShopResult.printResultPage(p, search, result, 1);

            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "No items found for your search");
            }
        } else {
            p.sendMessage(Chat.getPrefix() + ChatColor.RED + "No items in shops");
        }
    }
}
