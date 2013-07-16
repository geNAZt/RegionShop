package com.geNAZt.RegionShop.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.07.13
 */
public abstract class Listener {
    public void execute(PlayerJoinEvent event) {}
    public void execute(PlayerMoveEvent event) {}
    public void execute(PlayerInteractEvent event) {}
    public void execute(BlockBreakEvent event) {}
    public void execute(BlockPhysicsEvent event) {}
    public void execute(SignChangeEvent event) {}
    public void execute(PlayerQuitEvent event) {}
    public void execute(PlayerDropItemEvent event) {}
}
