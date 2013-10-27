package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.Chest;
import com.geNAZt.RegionShop.Database.Table.CustomerSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.Sign;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignDestroy implements Listener {
    @EventHandler
    public void onBlockBrak(BlockBreakEvent  event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        checkDestroy(event, true);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getBlock().getWorld().getName())) {
            return;
        }

        checkDestroy(event, false);
    }

    private void checkDestroy(BlockEvent event, boolean playerBreak) {
        Block b = event.getBlock();
        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
            Sign s = (Sign) b.getState().getData();

            Block attachedBlock = b.getRelative(s.getAttachedFace());
            if (attachedBlock.getType() == Material.AIR || playerBreak) {
                BlockBreakEvent event1 = null;
                if(playerBreak) {
                    event1 = (BlockBreakEvent) event;
                }

                //Look for ChestShops
                Chest chest = Database.getServer().find(Chest.class).
                        where().
                            conjunction().
                                eq("world", event.getBlock().getWorld().getName()).
                                eq("signX", event.getBlock().getX()).
                                eq("signY", event.getBlock().getY()).
                                eq("signZ", event.getBlock().getZ()).
                            endJunction().
                        findUnique();

                if(chest != null) {
                    if(playerBreak) {
                        if(chest.getOwners().iterator().next().getName().equals(event1.getPlayer().getName().toLowerCase()) || event1.getPlayer().hasPermission("rs.bypass.destroy.chestshop")) {
                            com.geNAZt.RegionShop.Database.Model.Chest.remove(chest);
                        } else {
                            event1.setCancelled(true);
                        }
                    } else {
                        com.geNAZt.RegionShop.Database.Model.Chest.remove(chest);
                    }
                }

                //Look for Customer Signs
                CustomerSign customerSign = Database.getServer().find(CustomerSign.class).
                        where().
                            conjunction().
                                eq("item.itemStorage.regions.world", event.getBlock().getWorld().getName()).
                                eq("x", event.getBlock().getX()).
                                eq("y", event.getBlock().getY()).
                                eq("z", event.getBlock().getZ()).
                            endJunction().
                        findUnique();

                if(customerSign != null) {
                    if(playerBreak) {
                        if(customerSign.getOwner().equals(event1.getPlayer().getName().toLowerCase()) || event1.getPlayer().hasPermission("rs.bypass.destroy.customersign")) {
                            for (final Entity ent : Bukkit.getWorld(customerSign.getRegion().getWorld()).getEntities()) {
                                //Get the location of this Entity
                                Location entLocation = ent.getLocation();
                                if (entLocation.getBlockZ() == customerSign.getZ() && entLocation.getBlockY() == customerSign.getY() - 1 && entLocation.getBlockX() == customerSign.getX()) {
                                    ent.remove();
                                }
                            }

                            Database.getServer().delete(customerSign);
                        } else {
                            event1.setCancelled(true);
                        }
                    } else {
                        for (final Entity ent : Bukkit.getWorld(customerSign.getRegion().getWorld()).getEntities()) {
                            //Get the location of this Entity
                            Location entLocation = ent.getLocation();
                            if (entLocation.getBlockZ() == customerSign.getZ() && entLocation.getBlockY() == customerSign.getY() - 1 && entLocation.getBlockX() == customerSign.getX()) {
                                ent.remove();
                            }
                        }

                        Database.getServer().delete(customerSign);
                    }
                }
            }
        }
    }
}
