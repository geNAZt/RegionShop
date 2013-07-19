package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.ListStorage;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopList extends ShopCommand {
    private final RegionShopPlugin plugin;

    public ShopList(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List items in the shop (inside a shopregion)", ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List all shops (outside a shopregion)"};
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public String getPermissionNode() {
        return "rs.list";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        //Check for optional Args
        Integer page = 1;

        if(args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Only numbers as page value");
                return;
            }
        }

        //Check if Player is inside a Region
        if (PlayerStorage.has(player)) {
            executeInsideRegion(player, page);
        } else {
            executeOutsideRegion(player, page);
        }
    }

    private void executeOutsideRegion(Player player, Integer page) {
        Integer skip = (page - 1) * 7;
        Integer current = 0;

        HashMap<String, ProtectedRegion> pRC = ListStorage.getUnique(player.getWorld());

        Integer maxPage = 0;
        if (pRC != null) {
            Float max = pRC.size() / (float)7;
            maxPage = (int)Math.ceil(max);
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of shops " + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.YELLOW + " --");
        player.sendMessage(Chat.getPrefix() + " ");

        if(pRC == null || pRC.isEmpty()) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No shops available");
            return;
        }

        for( Map.Entry<String, ProtectedRegion> rg : pRC.entrySet()) {
            DefaultDomain owners = rg.getValue().getOwners();

            current++;

            if(skip > current) {
                continue;
            }

            if(current - skip > 7) {
                return;
            }


            player.sendMessage(Chat.getPrefix() + ChatColor.GREEN + rg.getKey() + ChatColor.GOLD + " - Owners: " + ChatColor.GRAY + StringUtils.join(owners.getPlayers().toArray(), ", "));
        }

        if(page < maxPage) {
            player.setDisplayName(Chat.getPrefix() + ChatColor.GREEN + "/shop list " + (page+1) + ChatColor.GOLD + "for the next page");
        }

    }

    private void executeInsideRegionPlayerShop(Player player, Integer page, Region region) {
        PagingList<ShopItems> shopItems;

        //Check if Player is Owner
        if (region.getRegion().isOwner(player.getName())) {
            //Player is owner of this shop. he can see not ready items
            shopItems = plugin.getDatabase().find(ShopItems.class).
                    where().
                        conjunction().
                        eq("world", player.getWorld().getName()).
                        eq("region", region.getItemStorage()).
                            disjunction().
                                conjunction().
                                    gt("unit_amount", 0).
                                    disjunction().
                                        gt("sell", 0).
                                        gt("buy", 0).
                                    endJunction().
                                endJunction().
                                eq("owner", player.getName()).
                            endJunction().
                        endJunction().
                    findPagingList(7);
        } else {
            //Is normal player. Can only see ready items
            shopItems = plugin.getDatabase().find(ShopItems.class).
                    where().
                        conjunction().
                            eq("world", player.getWorld().getName()).
                            eq("region", region.getItemStorage()).
                            gt("unit_amount", 0).
                            disjunction().
                                gt("sell", 0).
                                gt("buy", 0).
                            endJunction().
                        endJunction().
                    findPagingList(7);
        }

        Integer curPage = page - 1;

        //Check if Valid page
        if (curPage < 0 || (curPage > shopItems.getTotalPageCount() - 1 && shopItems.getTotalPageCount() != 0)) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid page");
            return;
        }

        //Get the right page
        Page qryPage = shopItems.getPage(curPage);
        List<ShopItems> itemList = qryPage.getList();

        //Send the Header
        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of items in " + ChatColor.GREEN + region.getName() + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + (curPage+1) + ChatColor.GOLD + "/" + ChatColor.RED + shopItems.getTotalPageCount() + ChatColor.YELLOW + " --");

        String ench = Character.toString((char)0x2692);
        String dmg = Character.toString((char)0x26A0);
        String name = Character.toString((char)0x270E);
        String notrdy = Character.toString((char)0x2716);

        //Define the legend
        String legend = Chat.getPrefix() + ChatColor.YELLOW + "Legend: " + ChatColor.RED + dmg + ChatColor.YELLOW + " damaged, " + ChatColor.RED + ench + ChatColor.YELLOW + " enchanted, " + ChatColor.RED + name + ChatColor.YELLOW + " renamed";

        if (region.getRegion().isOwner(player.getName())) {
            legend += ", " + ChatColor.RED + notrdy + ChatColor.YELLOW + " not ready";
        }

        player.sendMessage(legend);
        player.sendMessage(Chat.getPrefix() + " ");

        //List all Items
        if(itemList.size() > 0) {
            for(ShopItems item : itemList) {
                if(((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) && !region.getRegion().isOwner(player.getName())) {
                    continue;
                }

                ItemStack iStack = ItemConverter.fromDBItem(item);

                String amount = item.getCurrentAmount().toString();
                String niceItemName = ItemName.nicer(iStack.getType().toString());
                String itemName = ItemName.getDataName(iStack) + niceItemName;

                String message = Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + item.getSell() + "$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + item.getBuy() + "$/" + item.getUnitAmount() + ChatColor.DARK_GREEN + " Unit(s) from " + ChatColor.GREEN + item.getOwner() + ChatColor.DARK_GREEN + " (" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_GREEN + ")";

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

                player.sendMessage(message);
            }

            if (qryPage.hasNext()) {
                player.sendMessage(Chat.getPrefix() + ChatColor.GREEN +"/shop list "+ (curPage+2) + ChatColor.GOLD + " for the next page");
            }
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This shop has no items");
        }
    }

    private void executeInsideRegionServerShop(Player player, Integer page, ConcurrentHashMap<ItemStack, Price> items, Region region) {
        Integer skip = (page - 1) * 7;
        Integer current = 0;

        Integer maxPage;
        Float max = items.size() / (float)7;
        maxPage = (int)Math.ceil(max);


        if(page < 1 || page > maxPage) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid page");
            return;
        }

        //Send the Header
        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of items in " + ChatColor.GREEN + region.getName() + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.YELLOW + " --");

        player.sendMessage(Chat.getPrefix() + " ");

        String endless = Character.toString((char)0x221E);

        for(Map.Entry<ItemStack, Price> item : items.entrySet()) {
            current++;

            if(skip > current) {
                continue;
            }

            if(current - skip > 7) {
                return;
            }

            String niceItemName = ItemName.nicer(item.getKey().getType().toString());
            String itemName = ItemName.getDataName(item.getKey()) + niceItemName;
            String itemID = Integer.toString(item.getKey().getTypeId());

            if(item.getKey().getData().getData() > 0) {
                itemID += ":" + item.getKey().getData().getData();
            }

            String message = Chat.getPrefix() + ChatColor.DARK_GREEN + endless + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + item.getValue().getCurrentSell() + "$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + item.getValue().getCurrentBuy() + "$/1" + ChatColor.DARK_GREEN + " Unit(s) from " + ChatColor.GREEN + "Server" + ChatColor.DARK_GREEN + " (" + ChatColor.GRAY + itemID + ChatColor.DARK_GREEN + ")";

            player.sendMessage(message);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void executeInsideRegion(Player player, Integer page) {
        Region region = PlayerStorage.get(player);
        ConcurrentHashMap<ItemStack, Price> serverShop = PriceStorage.getRegion(region.getRegion().getId());

        if(serverShop != null) {
            executeInsideRegionServerShop(player, page, serverShop, region);
        } else {
            executeInsideRegionPlayerShop(player, page, region);
        }
    }
}
