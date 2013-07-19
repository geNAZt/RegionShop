package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Core.Add;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Model.ShopSellSign;
import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignSellInteract extends Listener {
    private final RegionShopPlugin plugin;

    public SignSellInteract(RegionShopPlugin pl) {
        plugin = pl;
    }

    public void execute(PlayerInteractEvent event) {
        Block blk = event.getClickedBlock();
        if(blk == null) {
            return;
        }

        plugin.getLogger().info(blk.toString());

        if(blk.getType().equals(Material.SIGN_POST) || blk.getType().equals(Material.WALL_SIGN)) {
            ShopSellSign sellSign = plugin.getDatabase().find(ShopSellSign.class).
                    where().
                        conjunction().
                            eq("world", event.getPlayer().getWorld().getName()).
                            eq("x", event.getClickedBlock().getX()).
                            eq("y", event.getClickedBlock().getY()).
                            eq("z", event.getClickedBlock().getZ()).
                        endJunction().
                    findUnique();

            if(sellSign == null) return;

            if (!PlayerStorage.has(event.getPlayer())) {
                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop region.");
                return;
            }

            Region region = PlayerStorage.get(event.getPlayer());

            if(!region.getRegion().isOwner(event.getPlayer().getName())) {
                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "You are not a owner in this shop. You can not add items to it.");
                return;
            }

            ItemStack itemInHand = event.getPlayer().getItemInHand();

            //Check if the User has something in his hand
            if(itemInHand == null || itemInHand.getType().getId() == 0) {
                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You have no item in the hand");
                return;
            }

            Integer itemID;
            if((itemID = Add.add(itemInHand, event.getPlayer(), region, sellSign.getSell(), sellSign.getBuy(), sellSign.getAmount())) == 0) {
                //Remove the item from the Player
                event.getPlayer().getInventory().remove(itemInHand);

                //Get the nice name
                String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
                if (itemInHand.getItemMeta().hasDisplayName()) {
                    itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
                }

                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
            } else {
                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "Item already added. " + ChatColor.DARK_RED + "/shop set "+ itemID + " sellprice buyprice amount" + ChatColor.RED + " to change it.");
            }
        }
    }
}
