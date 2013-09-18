package com.geNAZt.RegionShop.Interface.Sign.Interact;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Core.Buy;
import com.geNAZt.RegionShop.Core.Sell;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.CustomerSign;
import com.geNAZt.RegionShop.Database.Table.Region;
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
public class Customer implements Listener {
    @EventHandler
    public void onSignInteract(SignInteract event) {
        CustomerSign customerSign = Database.getServer().find(CustomerSign.class).
                where().
                    conjunction().
                        eq("region.world", event.getParent().getPlayer().getWorld().getName()).
                        eq("x", event.getBlock().getX()).
                        eq("y", event.getBlock().getY()).
                        eq("z", event.getBlock().getZ()).
                    endJunction().
                findUnique();

        if(customerSign == null) return;

        if (!InRegion.has(event.getParent().getPlayer())) {
            event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Customer_NotInRegion);
            return;
        }

        Region region = InRegion.get(event.getParent().getPlayer());

        if(event.getParent().getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            ItemStack itemStack = event.getParent().getPlayer().getItemInHand();

            if(itemStack == null || itemStack.getType().getId() == 0) {
                event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Customer_Sell_NoItemInHand);
                return;
            }

            if(itemStack.getTypeId() != customerSign.getItem().getMeta().getId().getItemID() || itemStack.getData().getData() != customerSign.getItem().getMeta().getId().getDataValue()) {
                event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Customer_Sell_WrongItem);
                return;
            }

            Sell.sell(itemStack, customerSign.getItem(), event.getParent().getPlayer(), region);
        } else if(event.getParent().getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Buy.buy(customerSign.getItem(), event.getParent().getPlayer(), region, customerSign.getItem().getUnitAmount());
        }
    }
}
