package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.PlayerStorage;
import com.geNAZt.RegionShop.Util.WorldGuardBridge;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

                ShopItems item = plugin.getDatabase().find(ShopItems.class).
                        where().
                            conjunction().
                            eq("world", p.getWorld().getName()).
                            eq("region", region).
                            eq("item_id", itemInHand.getType().getId()).
                            eq("data_id", itemInHand.getData().getData()).
                            eq("durability", itemInHand.getDurability()).

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

                    newItem.setBuy(buy);
                    newItem.setSell(sell);
                    newItem.setUnitAmount(amount);

                    plugin.getDatabase().save(newItem);

                    p.getInventory().remove(itemInHand);

                    String itemName;
                    if (itemInHand.getItemMeta().hasDisplayName()) {
                        itemName = itemInHand.getItemMeta().getDisplayName();
                    } else {
                        itemName = itemInHand.getType().toString();
                    }

                    p.sendMessage("Added "+ ItemName.nicer(itemName) + " to the shop.");
                }
            } else {
                p.sendMessage("You aren't a owner in this Shop. You can't add items to it.");
            }

            return true;
        }



        //Nothing of all
        p.sendMessage("You haven't selected an Shop.");
        return false;
    }
}
