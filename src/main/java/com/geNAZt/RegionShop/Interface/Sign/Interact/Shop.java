package com.geNAZt.RegionShop.Interface.Sign.Interact;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Core.Buy;
import com.geNAZt.RegionShop.Core.Sell;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Chest;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Events.SignInteract;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 19.07.13
 */
public class Shop implements Listener {
    @EventHandler
    public void onSignInteract(SignInteract event) {
        Chest chest = Database.getServer().find(Chest.class).
                where().
                    conjunction().
                        eq("world", event.getParent().getPlayer().getWorld().getName()).
                        eq("signX", event.getBlock().getX()).
                        eq("signY", event.getBlock().getY()).
                        eq("signZ", event.getBlock().getZ()).
                    endJunction().
                findUnique();

        if(chest == null) return;

        if(event.getParent().getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getParent().getPlayer().hasPermission("rs.sign.shop.sell")) {
            ItemStack itemStack = event.getParent().getPlayer().getItemInHand();

            if(itemStack == null || itemStack.getType().getId() == 0) {
                event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Shop_Sell_NoItemInHand);
                return;
            }

            Items item = chest.getItemStorage().getItems().iterator().next();

            if(itemStack.getTypeId() != item.getMeta().getId().getItemID() || itemStack.getData().getData() != item.getMeta().getId().getDataValue()) {
                event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Shop_Sell_WrongItem);
                return;
            }

            Sell.sell(itemStack, item, event.getParent().getPlayer(), chest);
        } else if(event.getParent().getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getParent().getPlayer().hasPermission("rs.shop.sign.buy")) {
            Items item = chest.getItemStorage().getItems().iterator().next();
            Buy.buy(item, event.getParent().getPlayer(), chest, item.getUnitAmount());
        }
    }
}
