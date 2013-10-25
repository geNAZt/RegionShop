package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Events.SignInteract;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignInteractPrepare implements Listener {
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        Block blk = event.getClickedBlock();
        if(blk == null) {
            return;
        }

        if(blk.getType().equals(Material.SIGN_POST) || blk.getType().equals(Material.WALL_SIGN)) {
            SignInteract signInteract = new SignInteract(blk, event);

            Bukkit.getPluginManager().callEvent(signInteract);
        }
    }
}
