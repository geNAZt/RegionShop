package net.cubespace.RegionShop.Interface.Sign.Interact;

import com.j256.ormlite.stmt.QueryBuilder;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Core.Buy;
import net.cubespace.RegionShop.Core.Sell;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.CustomerSign;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Events.SignInteract;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class Customer implements Listener {
    @EventHandler
    public void onSignInteract(final SignInteract event) {
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                CustomerSign customerSign = null;
                try {
                    QueryBuilder<Region, Integer> regionQb = Database.getDAO(Region.class).queryBuilder();
                    regionQb.where().eq("world", event.getParent().getPlayer().getWorld().getName());

                    customerSign = Database.getDAO(CustomerSign.class).queryBuilder().
                            join(regionQb).
                            where().
                            eq("x", event.getBlock().getX()).
                            and().
                            eq("y", event.getBlock().getY()).
                            and().
                            eq("z", event.getBlock().getZ()).
                            queryForFirst();
                } catch (SQLException e) {
                    Logger.error("Could not get Customer Sign", e);
                }

                if (customerSign == null) return;

                if (!InRegion.has(event.getParent().getPlayer())) {
                    event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Customer_NotInRegion);
                    return;
                }

                Region region = InRegion.get(event.getParent().getPlayer());

                if (event.getParent().getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getParent().getPlayer().hasPermission("rs.sign.customer.sell")) {
                    ItemStack itemStack = event.getParent().getPlayer().getItemInHand();

                    if (itemStack == null || itemStack.getType().getId() == 0) {
                        event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Customer_Sell_NoItemInHand);
                        return;
                    }

                    if (itemStack.getTypeId() != customerSign.getItem().getMeta().getItemID() || itemStack.getData().getData() != customerSign.getItem().getMeta().getDataValue()) {
                        event.getParent().getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Interact_Customer_Sell_WrongItem);
                        return;
                    }

                    Sell.sell(itemStack, customerSign.getItem(), event.getParent().getPlayer(), region);
                } else if (event.getParent().getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getParent().getPlayer().hasPermission("rs.sign.customer.buy")) {
                    Buy.buy(customerSign.getItem(), event.getParent().getPlayer(), region, customerSign.getItem().getUnitAmount());
                }
            }
        });
    }
}
