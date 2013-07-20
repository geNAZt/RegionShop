package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Data.Storages.SignChestEquipStorage;
import com.geNAZt.RegionShop.Database.Model.ShopChestEquipSign;
import com.geNAZt.RegionShop.Database.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Database.Model.ShopSellSign;
import com.geNAZt.RegionShop.Interface.CLI.Shop.ShopEquip;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.Sign;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignDestroy extends Listener {
    private final RegionShopPlugin plugin;

    public SignDestroy(RegionShopPlugin pl) {
        plugin = pl;
    }

    public void execute(BlockBreakEvent  event) {
        checkDestroy(event, true);
    }

    public void execute(BlockPhysicsEvent event) {
        checkDestroy(event, false);
    }

    private void checkDestroy(BlockEvent event, boolean playerBreak) {
        Block b = event.getBlock();
        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
            Sign s = (Sign) b.getState().getData();

            Block attachedBlock = b.getRelative(s.getAttachedFace());
            if (attachedBlock.getType() == Material.AIR || playerBreak) {
                //Look for ChestEquip Signs
                ShopChestEquipSign chestEquipSign = plugin.getDatabase().find(ShopChestEquipSign.class).
                        where().
                            conjunction().
                                eq("world", event.getBlock().getWorld().getName()).
                                eq("x", event.getBlock().getX()).
                                eq("y", event.getBlock().getY()).
                                eq("z", event.getBlock().getZ()).
                            endJunction().
                        findUnique();

                if(chestEquipSign != null) {
                    plugin.getDatabase().delete(chestEquipSign);
                    SignChestEquipStorage.removeSign(b);
                    return;
                }

                //Look for SellSigns
                ShopSellSign sellSign = plugin.getDatabase().find(ShopSellSign.class).
                        where().
                            conjunction().
                                eq("world", event.getBlock().getWorld().getName()).
                                eq("x", event.getBlock().getX()).
                                eq("y", event.getBlock().getY()).
                                eq("z", event.getBlock().getZ()).
                            endJunction().
                        findUnique();

                if(sellSign != null) {
                    plugin.getDatabase().delete(sellSign);
                    return;
                }

                //Look for Equip Signs
                ShopEquipSign equipSign = plugin.getDatabase().find(ShopEquipSign.class).
                        where().
                            conjunction().
                                eq("world", event.getBlock().getWorld().getName()).
                                eq("x", event.getBlock().getX()).
                                eq("y", event.getBlock().getY()).
                                eq("z", event.getBlock().getZ()).
                            endJunction().
                        findUnique();

                if(equipSign != null) {
                    plugin.getDatabase().delete(equipSign);
                }
            }
        }
    }
}
