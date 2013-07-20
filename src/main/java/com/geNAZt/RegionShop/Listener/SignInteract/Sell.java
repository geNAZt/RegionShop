package com.geNAZt.RegionShop.Listener.SignInteract;

import com.geNAZt.RegionShop.Bukkit.Events.SignInteract;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Core.Add;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Model.ShopAddSign;
import com.geNAZt.RegionShop.Listener.Listener;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 19.07.13
 */
public class Sell extends Listener {
    private final RegionShopPlugin plugin;

    public Sell(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(SignInteract event) {
        ShopAddSign sellSign = plugin.getDatabase().find(ShopAddSign.class).
                where().
                    conjunction().
                        eq("world", event.getParent().getPlayer().getWorld().getName()).
                        eq("x", event.getBlock().getX()).
                        eq("y", event.getBlock().getY()).
                        eq("z", event.getBlock().getZ()).
                    endJunction().
                findUnique();

        if(sellSign == null) return;

        if (!PlayerStorage.has(event.getParent().getPlayer())) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop region.");
            return;
        }

        Region region = PlayerStorage.get(event.getParent().getPlayer());

        if(!region.getRegion().isOwner(event.getParent().getPlayer().getName())) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not a owner in this shop. You can not add items to it.");
            return;
        }

        ItemStack itemInHand = event.getParent().getPlayer().getItemInHand();

        //Check if the User has something in his hand
        if(itemInHand == null || itemInHand.getType().getId() == 0) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You have no item in the hand");
            return;
        }

        Integer itemID;
        if((itemID = Add.add(itemInHand, event.getParent().getPlayer(), region, sellSign.getSell(), sellSign.getBuy(), sellSign.getAmount())) == 0) {
            //Remove the item from the Player
            event.getParent().getPlayer().getInventory().remove(itemInHand);

            //Get the nice name
            String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
            if (itemInHand.getItemMeta().hasDisplayName()) {
                itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
            }

            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
        } else {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "Item already added. " + ChatColor.DARK_RED + "/shop set "+ itemID + " sellprice buyprice amount" + ChatColor.RED + " to change it.");
        }
    }
}
