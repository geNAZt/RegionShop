package net.cubespace.RegionShop.Events;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignInteract extends Event {
    private static final HandlerList handlers = new HandlerList();
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

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
