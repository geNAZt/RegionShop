package com.geNAZt.RegionShop.Command.Shop;

import com.geNAZt.RegionShop.Command.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopSet extends ShopCommand {
    private final Plugin plugin;

    public ShopSet(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public String getPermissionNode() {
        return "rs.stock.set";
    }

    @Override
    public int getNumberOfArgs() {
        return 3;
    }

    @Override
    public void execute(Player player, String[] args) {
        Integer buy, sell, amount, shopItemId;

        try {
            shopItemId = Integer.parseInt(args[0]);
            buy = Integer.parseInt(args[2]);
            sell = Integer.parseInt(args[1]);
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Only numbers as shopItemId, buy, sell and amount values allowed");
            return;
        }

        ShopItems item = plugin.getDatabase().
                find(ShopItems.class).
                where().
                    eq("id", shopItemId).
                    eq("owner", player.getName()).
                findUnique();

        if (item == null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This item could not be found or you aren't the owner of it.");
        } else {
            item.setBuy(buy);
            item.setSell(sell);
            item.setUnitAmount(amount);

            plugin.getDatabase().update(item);

            ItemStack itemStack = ItemConverter.fromDBItem(item);

            String itemName;
            if (itemStack.getItemMeta().hasDisplayName()) {
                itemName = ItemName.getDataName(itemStack) + itemStack.getItemMeta().getDisplayName();
            } else {
                itemName = ItemName.getDataName(itemStack) + itemStack.getType().toString();
            }

            player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Item " + ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " now sells for " + ChatColor.GREEN + item.getSell() + ChatColor.GOLD + ", buys for " + ChatColor.GREEN + item.getBuy() + ChatColor.GOLD + ", per " + ChatColor.GREEN + item.getUnitAmount() + ChatColor.GOLD + " unit(s)");
        }
    }
}
