package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Region.Region;
import com.geNAZt.RegionShop.Region.Resolver;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerMove implements Listener {
    private final RegionShopPlugin plugin;

    public PlayerMove(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        Region foundRegion = Resolver.resolve(e.getPlayer().getLocation(), e.getPlayer().getWorld());

        if (PlayerStorage.has(e.getPlayer())) {
            Region stored = PlayerStorage.get(e.getPlayer());
            if(foundRegion == null || stored.getRegion() != foundRegion.getRegion()) {
                e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have left " + ChatColor.DARK_GREEN + stored.getName() +  ChatColor.GOLD + ". Bye!");
                plugin.getLogger().info("Player "+ e.getPlayer().getDisplayName() + " left WG Region " + stored.getName());

                PlayerStorage.remove(e.getPlayer());

                if(foundRegion != null) {
                    e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + foundRegion.getName() +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                    plugin.getLogger().info("Player " + e.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());

                    PlayerStorage.set(e.getPlayer(), foundRegion);
                }
            }
        } else {
            if(foundRegion != null) {
                e.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + foundRegion.getName() +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                plugin.getLogger().info("Player " + e.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());

                PlayerStorage.set(e.getPlayer(), foundRegion);
            }
        }
    }
}
