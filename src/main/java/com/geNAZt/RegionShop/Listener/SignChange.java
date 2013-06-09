package com.geNAZt.RegionShop.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 09.06.13
 * Time: 08:54
 * To change this template use File | Settings | File Templates.
 */
public class SignChange {
    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
    }
}
