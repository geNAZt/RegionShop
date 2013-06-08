package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;

import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.SearchStorage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopSearch {
    private RegionShopPlugin plugin;

    public ShopSearch(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean execute(Player p, String search) {
        List<ShopItems> items = plugin.getDatabase().find(ShopItems.class).findList();
        if(items != null) {
            Pattern r = Pattern.compile("(.*)" + search + "(.*)");
            HashMap<ShopItems, ItemStack> result = new HashMap<ShopItems, ItemStack>();

            for(ShopItems item : items) {
                ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), 1);
                iStack.getData().setData(item.getDataID());
                iStack.setDurability(item.getDurability());

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

        return true;
    }
}
