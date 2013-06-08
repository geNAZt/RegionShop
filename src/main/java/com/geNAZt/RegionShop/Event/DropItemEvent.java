package com.geNAZt.RegionShop.Event;

import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.DropStorage;
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
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public class DropItemEvent implements Listener {
    private RegionShopPlugin plugin;

    public DropItemEvent(RegionShopPlugin pl) {
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
                ShopItems newItem = new ShopItems();
                newItem.setWorld(e.getPlayer().getWorld().getName());
                newItem.setCurrentAmount(droppedItem.getAmount());
                newItem.setItemID(droppedItem.getType().getId());
                newItem.setDurability(droppedItem.getDurability());
                newItem.setOwner(e.getPlayer().getName());
                newItem.setRegion(region);
                newItem.setDataID(droppedItem.getData().getData());
                newItem.setStackable(droppedItem.getMaxStackSize() != 1);
                newItem.setCustomName((droppedItem.getItemMeta().hasDisplayName()) ? droppedItem.getItemMeta().getDisplayName() : null);

                newItem.setBuy(0);
                newItem.setSell(0);
                newItem.setUnitAmount(0);

                plugin.getDatabase().save(newItem);

                Map<Enchantment, Integer> itemEnch = droppedItem.getEnchantments();
                if(itemEnch != null) {
                    for(Map.Entry<Enchantment, Integer> entry : itemEnch.entrySet()) {
                        ShopItemEnchantmens ench = new ShopItemEnchantmens();
                        ench.setEnchId(entry.getKey().getId());
                        ench.setEnchLvl(entry.getValue());
                        ench.setShopItemId(newItem.getId());

                        plugin.getDatabase().save(ench);
                    }
                }

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
