package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class BlockDestroy implements Listener {
    private final RegionShopPlugin plugin;

    public BlockDestroy(RegionShopPlugin pl) {
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent  event) {
        checkDestroy(event, true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        checkDestroy(event, false);
    }

    private void checkDestroy(BlockEvent event, boolean playerBreak) {
        Block b = event.getBlock();
        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
            Sign s = (Sign) b.getState().getData();
            Block attachedBlock = b.getRelative(s.getAttachedFace());
            if (attachedBlock.getType() == Material.AIR || playerBreak) {  // or maybe any non-solid material, but AIR is the normal case
                plugin.getServer().getPlayer("Skycrapper").sendMessage("Sign has been popped off");
            }
        }
    }
}
