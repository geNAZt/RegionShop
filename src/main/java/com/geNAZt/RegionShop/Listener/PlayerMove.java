package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Resolver;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerMove extends Listener {
    private final RegionShopPlugin plugin;

    public PlayerMove(RegionShopPlugin pl) {
        this.plugin = pl;
    }

    @Override
    public void execute(PlayerMoveEvent event) {
        Region foundRegion = Resolver.resolve(event.getPlayer().getLocation(), event.getPlayer().getWorld());

        if (PlayerStorage.has(event.getPlayer())) {
            Region stored = PlayerStorage.get(event.getPlayer());
            if(foundRegion == null || stored.getRegion() != foundRegion.getRegion()) {
                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have left " + ChatColor.DARK_GREEN + stored.getName() +  ChatColor.GOLD + ". Bye!");
                plugin.getLogger().info("Player "+ event.getPlayer().getDisplayName() + " left WG Region " + stored.getName());

                PlayerStorage.remove(event.getPlayer());

                if(foundRegion != null) {
                    event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + foundRegion.getName() +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                    plugin.getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());

                    PlayerStorage.set(event.getPlayer(), foundRegion);
                }
            }
        } else {
            if(foundRegion != null) {
                event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + foundRegion.getName() +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items");
                plugin.getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());

                PlayerStorage.set(event.getPlayer(), foundRegion);
            }
        }
    }
}
