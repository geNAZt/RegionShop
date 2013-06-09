package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Storages.DropStorage;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerDropItem implements Listener {
    private final RegionShopPlugin plugin;

    public PlayerDropItem(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (DropStorage.getPlayer(e.getPlayer()) != null) {
            String region = DropStorage.getPlayer(e.getPlayer());

            ItemStack droppedItem = e.getItemDrop().getItemStack();

            ShopItems item = plugin.getDatabase().find(ShopItems.class).
                    where().
                    conjunction().
                        eq("world", e.getPlayer().getWorld().getName()).
                        eq("region", region).
                        eq("item_id", droppedItem.getType().getId()).
                        eq("data_id", droppedItem.getData().getData()).
                        eq("durability", droppedItem.getDurability()).
                        eq("custom_name", (droppedItem.getItemMeta().hasDisplayName()) ? droppedItem.getItemMeta().getDisplayName() : null).
                    endJunction().
                    findUnique();

            if (item != null) {
                item.setCurrentAmount(item.getCurrentAmount() + droppedItem.getAmount());

                plugin.getDatabase().update(item);

                e.getItemDrop().remove();
            } else {
                ItemConverter.toDBItem(droppedItem, e.getPlayer().getWorld(), e.getPlayer().getName(), region, 0, 0, 0);
                e.getItemDrop().remove();

                String itemName;
                if (droppedItem.getItemMeta().hasDisplayName()) {
                    itemName = ItemName.getDataName(droppedItem) + droppedItem.getItemMeta().getDisplayName();
                } else {
                    itemName = ItemName.getDataName(droppedItem) + droppedItem.getType().toString();
                }

                e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
            }
        }
    }
}
