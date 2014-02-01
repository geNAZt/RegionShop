package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Repository.RegionRepository;
import net.cubespace.RegionShop.Database.Table.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PlayerMove implements Listener {
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                //Check if Event is in an enabled world
                if (!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
                    return;
                }

                //Since we only do DB operations we can do it async
                final Region foundRegion = RegionRepository.isIn(event.getPlayer());

                if (InRegion.has(event.getPlayer())) {
                    final Region stored = InRegion.get(event.getPlayer());
                    if (foundRegion == null || !stored.getRegion().equals(foundRegion.getRegion())) {
                        //Put something out => Needs to be synced
                        event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Leave.replace("%name", stored.getName()));
                        Plugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " left WG Region " + stored.getName());

                        InRegion.remove(event.getPlayer());

                        //Has User stepped from one region into another ?
                        if (foundRegion != null) {
                            //Put something out => Needs to be synced
                            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Enter.replace("%name", foundRegion.getName()));
                            Plugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());
                            InRegion.put(event.getPlayer(), foundRegion);
                        }
                    }
                } else {
                    //Has User stepped into a new Region ?
                    if (foundRegion != null) {
                        event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Shop_Enter.replace("%name", foundRegion.getName()));
                        Plugin.getInstance().getLogger().info("Player " + event.getPlayer().getDisplayName() + " entered WG Region " + foundRegion.getName());

                        InRegion.put(event.getPlayer(), foundRegion);
                    }
                }
            }
        });
    }
}
