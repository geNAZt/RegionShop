package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Chest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.06.13
 */
public class CheckChestProtection implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block blk = event.getClickedBlock();

            if(blk.getType().equals(Material.CHEST)) {
                Chest chest;
                if((chest = isRegionShopChest(event.getPlayer().getWorld(), blk.getLocation())) != null) {
                    if(!event.getPlayer().getName().toLowerCase().equals(chest.getOwners().iterator().next().getName()) && !event.getPlayer().hasPermission("rs.bypass.chestshop")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Protection_Chest_NotOwner);
                    }
                }
            }
        }
    }

    private Chest isRegionShopChest(World world, Location loc) {
        return Database.getServer().find(Chest.class).
                where().
                    eq("world", world.getName()).
                    eq("chestX", loc.getBlockX()).
                    eq("chestY", loc.getBlockY()).
                    eq("chestZ", loc.getBlockZ()).
                findUnique();
    }
}
