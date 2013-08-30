package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Events.SignInteract;
import com.geNAZt.RegionShop.Bukkit.ListenerManager;
import com.geNAZt.RegionShop.debugger.Profiler.Profiler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignInteractPrepare extends Listener {
    public void execute(PlayerInteractEvent event) {
        Block blk = event.getClickedBlock();
        if(blk == null) {
            return;
        }

        if(blk.getType().equals(Material.SIGN_POST) || blk.getType().equals(Material.WALL_SIGN)) {
            Profiler.start("SignInteractEvent");

            SignInteract signInteract = new SignInteract(blk, event);

            ArrayList<Listener> listener = ListenerManager.getHandler(SignInteract.class);

            if(listener != null && !listener.isEmpty()) {
                for(com.geNAZt.RegionShop.Listener.Listener listener1 : listener) {
                    listener1.execute(signInteract);
                }
            }

            Profiler.end("SignInteractEvent");
        }
    }
}
