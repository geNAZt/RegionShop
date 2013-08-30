package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Data.Storages.DropStorage;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.SearchStorage;
import org.bukkit.event.player.PlayerQuitEvent;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerQuit extends Listener {
    public void execute(PlayerQuitEvent event) {
        if (PlayerStorage.has(event.getPlayer())) {
            PlayerStorage.remove(event.getPlayer());
        }

        if (DropStorage.has(event.getPlayer())) {
            DropStorage.remove(event.getPlayer());
        }

        SearchStorage.removeAllPlayer(event.getPlayer());
    }
}
