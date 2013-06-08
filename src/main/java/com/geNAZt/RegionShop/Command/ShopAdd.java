package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.PlayerStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;

import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopAdd {
    private RegionShopPlugin plugin;

    public ShopAdd(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, Integer buy, Integer sell, Integer amount) {
        if (PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);
            RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());
            if (rgMngr.getRegion(region).isOwner(p.getName())) {
                ItemStack itemInHand = p.getItemInHand();

                if(itemInHand == null) {
                    p.sendMessage(Chat.getPrefix() + "You have no Item in the Hand");
                }


                ShopItems item = plugin.getDatabase().find(ShopItems.class).
                        where().
                            conjunction().
                            eq("world", p.getWorld().getName()).
                            eq("region", region).
                            eq("item_id", itemInHand.getType().getId()).
                            eq("data_id", itemInHand.getData().getData()).
                            eq("durability", itemInHand.getDurability()).
                            eq("custom_name", (itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null).
                        endJunction().findUnique();

                if (item == null) {
                    ShopItems newItem = new ShopItems();
                    newItem.setWorld(p.getWorld().getName());
                    newItem.setCurrentAmount(itemInHand.getAmount());
                    newItem.setItemID(itemInHand.getType().getId());
                    newItem.setDurability(itemInHand.getDurability());
                    newItem.setOwner(p.getName());
                    newItem.setRegion(region);
                    newItem.setDataID(itemInHand.getData().getData());
                    newItem.setStackable(itemInHand.getMaxStackSize() != 1);
                    newItem.setCustomName((itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null);

                    newItem.setBuy(buy);
                    newItem.setSell(sell);
                    newItem.setUnitAmount(amount);

                    plugin.getDatabase().save(newItem);

                    Map<Enchantment, Integer> itemEnch = itemInHand.getEnchantments();
                    if(itemEnch != null) {
                        for(Map.Entry<Enchantment, Integer> entry : itemEnch.entrySet()) {
                            ShopItemEnchantmens ench = new ShopItemEnchantmens();
                            ench.setEnchId(entry.getKey().getId());
                            ench.setEnchLvl(entry.getValue());
                            ench.setShopItemId(newItem.getId());

                            plugin.getDatabase().save(ench);
                        }
                    }

                    p.getInventory().remove(itemInHand);

                    String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
                    if (itemInHand.getItemMeta().hasDisplayName()) {
                        itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
                    }

                    p.sendMessage(Chat.getPrefix() + "Added "+ ItemName.nicer(itemName) + " to the shop.");
                } else {
                    p.sendMessage(Chat.getPrefix() + "Item already added. /shop change "+ item.getId() + " to change it.");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + "You aren't a owner in this Shop. You can't add items to it.");
            }

            return true;
        }



        //Nothing of all
        p.sendMessage(Chat.getPrefix() + "You haven't selected an Shop.");
        return false;
    }
}
