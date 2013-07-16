package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.DropStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class DropEquip extends Listener {
    private final RegionShopPlugin plugin;

    public DropEquip(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    public void execute(PlayerDropItemEvent event) {
        if (DropStorage.has(event.getPlayer())) {
            Region region = DropStorage.get(event.getPlayer());

            ItemStack droppedItem = event.getItemDrop().getItemStack();

            ShopItems item = plugin.getDatabase().find(ShopItems.class).
                where().
                    conjunction().
                        eq("world", event.getPlayer().getWorld().getName()).
                        eq("region", region.getItemStorage()).
                        eq("item_id", droppedItem.getType().getId()).
                        eq("data_id", droppedItem.getData().getData()).
                        eq("durability", droppedItem.getDurability()).
                        eq("owner", event.getPlayer().getName()).
                        eq("custom_name", (droppedItem.getItemMeta().hasDisplayName()) ? droppedItem.getItemMeta().getDisplayName() : null).
                    endJunction().
                findUnique();

            if (item != null) {
                item.setCurrentAmount(item.getCurrentAmount() + droppedItem.getAmount());

                plugin.getDatabase().update(item);

                Transaction.generateTransaction(event.getPlayer(), ShopTransaction.TransactionType.EQUIP, region.getName(), item.getWorld(), item.getOwner(), item.getItemID(), droppedItem.getAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());

                event.getItemDrop().remove();
            } else {
                ItemConverter.toDBItem(droppedItem, event.getPlayer().getWorld(), event.getPlayer().getName(), region.getItemStorage(), 0, 0, 0);
                event.getItemDrop().remove();

                String itemName;
                if (droppedItem.getItemMeta().hasDisplayName()) {
                    itemName = ItemName.getDataName(droppedItem) + droppedItem.getItemMeta().getDisplayName();
                } else {
                    itemName = ItemName.getDataName(droppedItem) + droppedItem.getType().toString();
                }

                //noinspection ConstantConditions
                Transaction.generateTransaction(event.getPlayer(), ShopTransaction.TransactionType.EQUIP, region.getName(), event.getPlayer().getWorld().getName(), event.getPlayer().getName(), item.getItemID(), droppedItem.getAmount(), 0.0, 0.0, 0);

                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
            }
        }
    }
}
