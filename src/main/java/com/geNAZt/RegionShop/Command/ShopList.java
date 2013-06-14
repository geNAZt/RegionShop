package com.geNAZt.RegionShop.Command;

import com.avaje.ebean.*;
import com.geNAZt.RegionShop.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopList {
    private final RegionShopPlugin plugin;

    public ShopList(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p, String region, Integer page) {
        if (region == null) {
            Integer skip = (page - 1) * 7;
            Integer current = 0;

            ArrayList<ProtectedRegion> pRC = ListStorage.getShopList(p.getWorld());

            Integer maxPage = 0;
            if (pRC != null) {
                Float max = pRC.size() / (float)7;
                maxPage = (int)Math.ceil(max);
            }

            p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of shops " + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.YELLOW + " --");
            p.sendMessage(Chat.getPrefix() + " ");

            if(pRC.isEmpty()) {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "No shops available");
                return;
            }

            for( ProtectedRegion rg : pRC) {
                DefaultDomain owners = rg.getOwners();

                current++;

                if(skip > current) {
                    continue;
                }

                if(current - skip > 7) {
                    return;
                }

                String shopName = WorldGuardBridge.convertRegionToShopName(rg, p.getWorld());
                p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + ((shopName != null) ? shopName : rg.getId()) + ChatColor.GOLD + " - Owners: " + ChatColor.GRAY + StringUtils.join(owners.getPlayers().toArray(), ", "));
            }

            if(page < maxPage) {
                p.setDisplayName(Chat.getPrefix() + ChatColor.GREEN + "/shop list " + (page+1) + ChatColor.GOLD + "for the next page");
            }

            return;
        }

        if (WorldGuardBridge.isRegion(region, p.getWorld())) {
            ProtectedRegion regionObj = WorldGuardBridge.getRegionByString(region, p.getWorld());
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
                        findPagingList(7);
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
                        findPagingList(7);
            }

            Integer curPage = page - 1;

            if (curPage < 0 || (curPage > shopItems.getTotalPageCount() - 1 && shopItems.getTotalPageCount() != 0)) {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid page");
                return;
            }

            Page qryPage = shopItems.getPage(curPage);
            List<ShopItems> itemList = qryPage.getList();

            plugin.getLogger().info("Got " + itemList.size() + " items in this shop");

            String shopName = WorldGuardBridge.convertRegionToShopName(regionObj, p.getWorld());
            if(shopName == null) {
                shopName = regionObj.getId();
            }

            p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of items in " + ChatColor.GREEN + shopName + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + (curPage+1) + ChatColor.GOLD + "/" + ChatColor.RED + shopItems.getTotalPageCount() + ChatColor.YELLOW + " --");

            String ench = Character.toString((char)0x2692);
            String dmg = Character.toString((char)0x26A0);
            String name = Character.toString((char)0x270E);
            String notrdy = Character.toString((char)0x2716);

            String legend = Chat.getPrefix() + ChatColor.YELLOW + "Legend: " + ChatColor.RED + dmg + ChatColor.YELLOW + " damaged, " + ChatColor.RED + ench + ChatColor.YELLOW + " enchanted, " + ChatColor.RED + name + ChatColor.YELLOW + " renamed";

            if (regionObj.isOwner(p.getName())) {
                legend += ", " + ChatColor.RED + notrdy + ChatColor.YELLOW + " not ready";
            }

            p.sendMessage(legend);
            p.sendMessage(Chat.getPrefix() + " ");
            if(itemList.size() > 0) {
                for(ShopItems item : itemList) {
                    if(((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) && !regionObj.isOwner(p.getName())) {
                        continue;
                    }

                    ItemStack iStack = ItemConverter.fromDBItem(item);

                    String amount = item.getCurrentAmount().toString();
                    String niceItemName = ItemName.nicer(iStack.getType().toString());
                    String itemName = ItemName.getDataName(iStack) + niceItemName;

                    String message = Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + item.getSell() + "$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + item.getBuy() + "$/" + item.getUnitAmount() + " Unit(s) from " + ChatColor.GREEN + item.getOwner() + ChatColor.GRAY + " #" + item.getId();

                    Integer perDmg = 0;

                    if (iStack.getDurability() > 0 && item.getItemID() != 373 && !item.isStackable()) {
                        Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                        perDmg = Math.round(divide * 100);
                    }

                    if (item.isStackable() && perDmg > 0) {
                        message += " " + ChatColor.RED + dmg;
                    }

                    if (!iStack.getEnchantments().isEmpty()) {
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
                    p.sendMessage(Chat.getPrefix() + ChatColor.GREEN +"/shop list "+ (curPage+2) + ChatColor.GOLD + " for the next page");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This shop has no items");
            }

            return;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid region");
    }
}
