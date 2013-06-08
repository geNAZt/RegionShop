package com.geNAZt.RegionShop.Command;

import com.avaje.ebean.*;
import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
            RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
            ProtectedRegion regionObj = rgMngr.getRegion(region);
            PagingList<ShopItems> shopItems;

            if (regionObj.isOwner(p.getName())) {
                shopItems = plugin.getDatabase().
                        find(ShopItems.class).
                        where().
                            conjunction().
                                eq("world", p.getWorld().getName()).
                                eq("region", region).
                                disjunction().
                                    conjunction().
                                        gt("unit_amount", 0).
                                        disjunction().
                                            gt("sell", 0).
                                            gt("buy", 0).
                                        endJunction().
                                    endJunction().
                                    eq("owner", p.getName()).
                                endJunction().
                            endJunction().
                        findPagingList(10);
            } else {
                shopItems= plugin.getDatabase().
                        find(ShopItems.class).
                        where().
                            conjunction().
                                eq("world", p.getWorld().getName()).
                                eq("region", region).
                                gt("unit_amount", 0).
                                disjunction().
                                    gt("sell", 0).
                                    gt("buy", 0).
                                endJunction().
                            endJunction().
                        findPagingList(10);
            }

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
            String notrdy = Character.toString((char)0x2716);

            String legend = Chat.getPrefix() + "Legend: " + ChatColor.RED + dmg + ChatColor.RESET + " Damaged Item, " + ChatColor.GREEN + ench + ChatColor.RESET + " Enchanted Item, " + ChatColor.YELLOW + name + ChatColor.RESET + " Custom Name";

            if (regionObj.isOwner(p.getName())) {
                legend += ", " + ChatColor.LIGHT_PURPLE + notrdy + ChatColor.RESET + " Item isn't ready to be sold";
            }

            p.sendMessage(legend);
            p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "/shop detail " + ChatColor.GRAY + "id" + ChatColor.RESET + " to show details - " + ChatColor.YELLOW + "/shop buy " + ChatColor.GRAY + "id" + ChatColor.RESET + " <amount> to buy an item");
            p.sendMessage(Chat.getPrefix() + " ");
            if(itemList.size() > 0) {
                for(ShopItems item : itemList) {
                    if(((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) && !regionObj.isOwner(p.getName())) {
                        continue;
                    }

                    ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), 1, item.getDurability());
                    iStack.getData().setData(item.getDataID());

                    String amount = item.getCurrentAmount().toString();
                    String niceItemName = ItemName.nicer(iStack.getType().toString());
                    String itemName = ItemName.getDataName(iStack) + niceItemName;

                    String message = Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + item.getSell() + "$/" + item.getUnitAmount() + " Unit(s) " + ChatColor.GRAY + "#" + item.getId();

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

                    if((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) {
                        message += " " + ChatColor.LIGHT_PURPLE + notrdy;
                    }

                    if(item.getCustomName() != null) {
                        message += " " + ChatColor.YELLOW + name;
                    }

                    p.sendMessage(message);
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
