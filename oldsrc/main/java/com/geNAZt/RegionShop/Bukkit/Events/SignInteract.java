package com.geNAZt.RegionShop.Bukkit.Events;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 19.07.13
 */
public class SignInteract {
    private final Block block;
    private final PlayerInteractEvent parent;

    public SignInteract(Block block, PlayerInteractEvent parent) {
        this.block = block;
        this.parent = parent;
    }

    public PlayerInteractEvent getParent() {
        return this.parent;
    }

    public Block getBlock() {
        return this.block;
    }
}
