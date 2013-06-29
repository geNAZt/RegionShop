package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Storages.SearchStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopResult extends ShopCommand {
    private static Plugin plugin;

    public ShopResult(Plugin pl) {
        plugin = pl;

    }

    @SuppressWarnings("ConstantConditions")
    public static void printResultPage(Player p, String searchQry, ConcurrentHashMap<ShopItems, ItemStack> result, Integer page) {
        Float max = (float)result.size() / (float)7;
        Integer maxPage = (int)Math.ceil(max);
        Integer skip = (page - 1) * 7;
        Integer current = 0;

        String ench = Character.toString((char)0x2692);
        String dmg = Character.toString((char)0x26A0);
        String name = Character.toString((char)0x270E);

        if (skip > result.size()) {
            p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid page");
            return;
        }


        p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Result for search: " + ChatColor.GREEN + searchQry + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.YELLOW + " --" );
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

            String shopName = WorldGuardBridge.convertRegionToShopName(WorldGuardBridge.getRegionByString(item.getRegion(), plugin.getServer().getWorld(item.getWorld())), plugin.getServer().getWorld(item.getWorld()));
            if(shopName == null) {
                shopName = item.getRegion();
            }

            String message = Chat.getPrefix() + ChatColor.DARK_GREEN + amount + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + item.getSell() + "$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + item.getBuy() + "$/" + item.getUnitAmount() + " Unit(s)" + ChatColor.DARK_GREEN + " at " + ChatColor.GREEN + shopName + " " + ChatColor.GRAY + "#" + item.getId();

            Integer enchant = plugin.getDatabase().find(ShopItemEnchantments.class).
                    where().
                        eq("shop_item_id", item.getId()).
                    findRowCount();

            Integer perDmg = 0;

            if (iStack.getDurability() > 0 && item.getItemID() != 373 && !item.isStackable()) {
                Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                perDmg = Math.round(divide * 100);
            }

            if (item.isStackable() && perDmg > 0) {
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

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop result " + ChatColor.RED + "page" + ChatColor.RESET + ": Browse to page " + ChatColor.RED + "page"};
    }

    @Override
    public String getCommand() {
        return "result";
    }

    @Override
    public String getPermissionNode() {
        return "rs.search";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        Integer page = 1;

        if(args.length > 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as page value allowed");
                return;
            }
        }

        if (SearchStorage.hasPlayer(player)) {
            ShopResult.printResultPage(player, SearchStorage.getSearchQuery(player), SearchStorage.getSearchResult(player), page);
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You have no results");
        }
    }
}
