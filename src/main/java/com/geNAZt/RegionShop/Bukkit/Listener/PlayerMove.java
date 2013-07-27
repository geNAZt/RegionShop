package com.geNAZt.RegionShop.Bukkit.Listener;

import com.geNAZt.RegionShop.Bukkit.ListenerManager;
import com.geNAZt.debugger.Profiler.Profiler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class PlayerMove implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        Profiler.start("PlayerMoveEvent");

        ArrayList<com.geNAZt.RegionShop.Listener.Listener> listener = ListenerManager.getHandler(e.getClass());

        if(listener != null && !listener.isEmpty()) {
            for(com.geNAZt.RegionShop.Listener.Listener listener1 : listener) {
                listener1.execute(e);
            }
        }

        Profiler.end("PlayerMoveEvent");
    }
}
