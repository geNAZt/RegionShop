package net.cubespace.RegionShop.Interface.Sign.Interact;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Core.Buy;
import net.cubespace.RegionShop.Core.Sell;
import net.cubespace.RegionShop.Database.Repository.ChestRepository;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Events.SignInteract;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Shop implements Listener {
    @EventHandler
    public void onSignInteract(final SignInteract event) {
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                Chest chest = ChestRepository.getViaSign(event.getBlock(), event.getParent().getPlayer().getWorld());

                if(chest == null) return;

                if(event.getParent().getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getParent().getPlayer().hasPermission("rs.sign.shop.sell")) {
                    ItemStack itemStack = event.getParent().getPlayer().getItemInHand();

                    if(itemStack == null || itemStack.getType().getId() == 0) {
                        event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Shop_Sell_NoItemInHand);
                        return;
                    }

                    Items item = chest.getItemStorage().getItems().iterator().next();

                    if(itemStack.getTypeId() != item.getMeta().getItemID() || itemStack.getData().getData() != item.getMeta().getDataValue()) {
                        event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Shop_Sell_WrongItem);
                        return;
                    }

                    Sell.sell(itemStack, item, event.getParent().getPlayer(), chest);
                } else if(event.getParent().getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getParent().getPlayer().hasPermission("rs.sign.shop.buy")) {
                    Items item = chest.getItemStorage().getItems().iterator().next();
                    Buy.buy(item, event.getParent().getPlayer(), chest, item.getUnitAmount());
                }
            }
        });
    }
}
