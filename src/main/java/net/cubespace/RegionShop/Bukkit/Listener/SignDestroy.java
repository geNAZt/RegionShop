package net.cubespace.RegionShop.Bukkit.Listener;

import com.j256.ormlite.stmt.QueryBuilder;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.CustomerSign;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Util.Logger;
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

import java.sql.SQLException;

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
                Chest chest = null;
                try {
                    chest = Database.getDAO(Chest.class).queryBuilder().
                            where().
                                eq("world", event.getBlock().getWorld().getName()).
                                and().
                                eq("signX", event.getBlock().getX()).
                                and().
                                eq("signY", event.getBlock().getY()).
                                and().
                                eq("signZ", event.getBlock().getZ()).
                                queryForFirst();
                } catch (SQLException e) {
                    Logger.error("Could not find Chest", e);
                }

                if(chest != null) {
                    if(playerBreak) {
                        if(chest.getOwners().iterator().next().getPlayer().getName().equals(event1.getPlayer().getName().toLowerCase()) || event1.getPlayer().hasPermission("rs.bypass.destroy.chestshop")) {
                            net.cubespace.RegionShop.Database.Repository.ChestRepository.remove(chest);
                        } else {
                            event1.setCancelled(true);
                        }
                    } else {
                        net.cubespace.RegionShop.Database.Repository.ChestRepository.remove(chest);
                    }
                }

                //Look for Customer Signs
                CustomerSign customerSign = null;
                try {
                    QueryBuilder<Region, Integer> regionQb = Database.getDAO(Region.class).queryBuilder();
                    regionQb.where().eq("world", event.getBlock().getWorld().getName());

                    customerSign = Database.getDAO(CustomerSign.class).queryBuilder().
                            join(regionQb).
                            where().
                            eq("x", event.getBlock().getX()).
                            and().
                            eq("y", event.getBlock().getY()).
                            and().
                            eq("z", event.getBlock().getZ()).
                            queryForFirst();
                } catch (SQLException e) {
                    Logger.error("Could not get Customer Sign", e);
                }


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

                            try {
                                Database.getDAO(CustomerSign.class).delete(customerSign);
                            } catch (SQLException e) {
                                Logger.error("Could not delete Customer Sign", e);
                                event1.setCancelled(true);
                            }
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

                        try {
                            Database.getDAO(CustomerSign.class).delete(customerSign);
                        } catch (SQLException e) {
                            Logger.error("Could not delete Customer Sign", e);
                        }
                    }
                }
            }
        }
    }
}
