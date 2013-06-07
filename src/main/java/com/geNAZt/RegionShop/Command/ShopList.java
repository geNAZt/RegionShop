package com.geNAZt.RegionShop.Command;

import com.avaje.ebean.*;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopList {
    private RegionShopPlugin plugin;

    public ShopList(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, String region, String page) {
        if (WorldGuardBridge.isRegion(region, p)) {
            PagingList<ShopItems> shopItems = plugin.getDatabase().
                    find(ShopItems.class).
                    where().
                        conjunction().
                            eq("world", p.getWorld().getName()).
                            eq("region", region).
                        endJunction().
                    findPagingList(10);

            Integer curPage;

            try {
                curPage = Integer.parseInt(page) - 1;
            } catch (NumberFormatException e) {
                curPage = 0;
            }

            if (curPage < 0) {
                p.sendMessage(Chat.getPrefix() + "Invalid Page");
                return false;
            }

            Page qryPage = shopItems.getPage(curPage);
            List<ShopItems> itemList = qryPage.getList();

            plugin.getLogger().info("Got " + itemList.size() + " items in this shop");

            p.sendMessage(Chat.getPrefix() + "|--- Page "+ (curPage+1) +"/"+ shopItems.getTotalPageCount() +" ---|");

            String ench = Character.toString((char)0x2692);
            String dmg = Character.toString((char)0x26A0);
            String name = Character.toString((char)0x270E);

            p.sendMessage(Chat.getPrefix() + "Legend: " + ChatColor.RED + dmg + ChatColor.RESET + " Damaged Item, " + ChatColor.GREEN + ench + ChatColor.RESET + " Enchanted Item, " + ChatColor.YELLOW + name + ChatColor.RESET + " Custom Name");
            p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "/shop detail " + ChatColor.GRAY + "id" + ChatColor.RESET + " to show details - " + ChatColor.YELLOW + "/shop buy " + ChatColor.GRAY + "id" + ChatColor.RESET + " <amount> to buy an item");
            p.sendMessage(Chat.getPrefix() + " ");
            if(itemList.size() > 0) {
                for(ShopItems item : itemList) {
                    ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), 1, item.getDurability());
                    iStack.getData().setData(item.getDataID());

                    String amount = item.getCurrentAmount().toString();
                    String niceItemName = ItemName.nicer(iStack.getType().toString());
                    String itemName = ItemName.getDataName(iStack) + niceItemName;

                    if (item.isStackable()) {
                        p.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + item.getSell() + "$/" + item.getUnitAmount() + " Unit(s) " + ChatColor.GRAY + "#" + item.getId());
                    } else {
                        p.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + item.getSell() + "$/" + item.getUnitAmount() + " Unit(s) " + ChatColor.GRAY + "#" + item.getId() + " " + ChatColor.RED + dmg);
                    }
                }

                if (qryPage.hasNext()) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.GREEN +"/shop list "+ (curPage+2) + ChatColor.RESET + " for the next Page");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + "This Shop hasnt any items");
            }

            return true;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + "Invalid Region");
        return false;
    }
}
