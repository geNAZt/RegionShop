package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Database.Repository.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class CheckForNewPlayer implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(!PlayerRepository.isStored(event.getPlayer())) {
                    PlayerRepository.insert(event.getPlayer());
                }
            }
        });
    }
}

