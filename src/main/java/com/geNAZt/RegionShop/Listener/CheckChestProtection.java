package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Database.Model.ShopChestEquipSign;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.06.13
 */
public class CheckChestProtection extends Listener {
    private final RegionShopPlugin plugin;

    public CheckChestProtection(RegionShopPlugin pl) {
        plugin = pl;
    }

    public void execute(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block blk = event.getClickedBlock();

            if(blk.getType().equals(Material.CHEST)) {
                Logger.debug("Clicked item is a Chest");
                ShopChestEquipSign equipSign;
                if((equipSign = isRegionShopChest(event.getPlayer().getWorld(), blk.getLocation())) != null) {
                    Logger.debug("Found DB Entry for this Chest: " + equipSign.getId());
                    if(!event.getPlayer().getName().equals(equipSign.getOwner()) || (plugin.getConfig().getBoolean("only-survival", true) && event.getPlayer().getGameMode() != GameMode.SURVIVAL)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not the owner of this Chest or you are in Creative Gamemode");
                    }
                }
            }

            if(blk.getType().equals(Material.SIGN_POST) || blk.getType().equals(Material.WALL_SIGN)) {
                ShopChestEquipSign equipSign;
                if((equipSign = isRegionShopChest(event.getPlayer().getWorld(), blk.getLocation())) != null) {
                    if(!event.getPlayer().getName().equals(equipSign.getOwner()) || (plugin.getConfig().getBoolean("only-survival", true) && event.getPlayer().getGameMode() != GameMode.SURVIVAL)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not the owner of this Chest or you are in Creative Gamemode");
                    }
                }
            }
        }
    }

    private ShopChestEquipSign isRegionShopChest(World world, Location loc) {
        for(Integer y = -1; y<2; y++) {
            for(Integer x = -1; x<2; x++) {
                for(Integer z = -1; z<2; z++) {
                    Integer newX = ((Double)loc.getX()).intValue() + x;
                    Integer newY = ((Double)loc.getY()).intValue() + y;
                    Integer newZ = ((Double)loc.getZ()).intValue() + z;

                    Material type = world.getBlockAt(newX, newY, newZ).getType();
                    if(type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                        plugin.getLogger().info("Checking: " + (loc.getX() + x) + " - " + (loc.getY() + y) + " - " + (loc.getZ() + z));

                        ShopChestEquipSign shopChestEquipSign = plugin.getDatabase().find(ShopChestEquipSign.class).
                                where().
                                    eq("world", world.getName()).
                                    eq("x", ((Double) loc.getX()).intValue() + x).
                                    eq("y", ((Double) loc.getY()).intValue() + y).
                                    eq("z", ((Double) loc.getZ()).intValue() + z).
                                findUnique();

                        if(shopChestEquipSign != null) {
                            return shopChestEquipSign;
                        }
                    }
                }
            }
        }

        return null;
    }
}
