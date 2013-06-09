package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Storages.PlayerStorage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopAdd {
    private final RegionShopPlugin plugin;

    public ShopAdd(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p, Integer buy, Integer sell, Integer amount) {
        if (PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);

            if (ListStorage.getShopByRegion(region, p.getWorld()).isOwner(p.getName())) {
                ItemStack itemInHand = p.getItemInHand();

                if(itemInHand == null || itemInHand.getType().getId() == 0) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have no item in the hand");
                    return;
                }

                ShopItems item = plugin.getDatabase().find(ShopItems.class).
                        where().
                            conjunction().
                                eq("world", p.getWorld().getName()).
                                eq("region", region).
                                eq("item_id", itemInHand.getType().getId()).
                                eq("data_id", itemInHand.getData().getData()).
                                eq("durability", itemInHand.getDurability()).
                                eq("owner", p.getName()).
                                eq("custom_name", (itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null).
                            endJunction().
                        findUnique();

                if (item == null) {
                    ItemConverter.toDBItem(itemInHand, p.getWorld(), p.getName(), region, buy, sell, amount);

                    p.getInventory().remove(itemInHand);

                    String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
                    if (itemInHand.getItemMeta().hasDisplayName()) {
                        itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
                    }

                    p.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
                    return;
                } else {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Item already added. /shop set "+ item.getId() + " <sell> <buy> <amount> to change it.");
                    return;
                }
            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You aren't a owner in this shop. You can't add items to it.");
                return;
            }
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You are not inside a shop region.");
    }
}
