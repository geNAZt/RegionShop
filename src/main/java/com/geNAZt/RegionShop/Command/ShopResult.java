package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.SearchStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;

import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopResult {
    private static RegionShopPlugin plugin;

    public ShopResult(RegionShopPlugin pl) {
        plugin = pl;

    }

    public boolean execute(Player p, Integer page) {
        if (SearchStorage.hasPlayer(p)) {
            ShopResult.printResultPage(p, SearchStorage.getSearchQuery(p), SearchStorage.getSearchResult(p), page);
        } else {
            p.sendMessage(Chat.getPrefix() + "You have no Results");
        }

        return true;
    }

    public static void printResultPage(Player p, String searchQry, HashMap<ShopItems, ItemStack> result, Integer page) {
        Float max = (float)result.size() / (float)7;
        Integer maxPage = (int)Math.ceil(max);
        Integer skip = (page - 1) * 7;
        Integer current = 0;

        String ench = Character.toString((char)0x2692);
        String dmg = Character.toString((char)0x26A0);
        String name = Character.toString((char)0x270E);

        if (skip > result.size()) {
            p.sendMessage(Chat.getPrefix() + "Invalid Page");
            return;
        }

        RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());

        p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Result for Search: " + ChatColor.GREEN + searchQry + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.YELLOW + " --" );
        p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "Legend: " + ChatColor.RED + dmg + ChatColor.YELLOW + " damaged, " + ChatColor.RED + ench + ChatColor.YELLOW + " enchanted, " + ChatColor.RED + name + ChatColor.YELLOW + " renamed");
        p.sendMessage(Chat.getPrefix() + " ");
        for(Map.Entry<ShopItems, ItemStack> entry : result.entrySet()) {
            current++;

            if(skip > current) {
                continue;
            }

            if(current - skip > 7) {
                return;
            }

            ShopItems item = entry.getKey();
            ItemStack iStack = entry.getValue();

            String amount = item.getCurrentAmount().toString();
            String niceItemName = ItemName.nicer(iStack.getType().toString());
            String itemName = ItemName.getDataName(iStack) + niceItemName;

            String message = Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + item.getSell() + "$/" + item.getUnitAmount() + " Unit(s)" + ChatColor.DARK_GREEN + " at " + ChatColor.GREEN + WorldGuardBridge.convertRegionToShopName(rgMngr.getRegion(item.getRegion()), plugin.getServer().getWorld(item.getWorld())) + " " + ChatColor.GRAY + "#" + item.getId();

            Integer enchant = plugin.getDatabase().find(ShopItemEnchantmens.class).
                    where().
                        eq("shop_item_id", item.getId()).
                    findRowCount();

            Integer perDmg = 0;

            if (iStack.getDurability() > 0 && item.getItemID() != 373) {
                Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                perDmg = Math.round(divide * 100);
            }

            if (!item.isStackable() && perDmg > 0) {
                message += " " + ChatColor.RED + dmg;
            }

            if (enchant > 0) {
                message += " " + ChatColor.GREEN + ench;
            }

            if(item.getCustomName() != null) {
                message += " " + ChatColor.YELLOW + name;
            }

            p.sendMessage(message);
        }
    }
}
