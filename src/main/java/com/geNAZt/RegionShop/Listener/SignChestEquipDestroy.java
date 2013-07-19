package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Data.Storages.SignChestEquipStorage;
import com.geNAZt.RegionShop.Database.Model.ShopChestEquipSign;
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
public class SignChestEquipDestroy extends Listener {
    private final RegionShopPlugin plugin;

    public SignChestEquipDestroy(RegionShopPlugin pl) {
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
                ShopChestEquipSign equipSign = plugin.getDatabase().find(ShopChestEquipSign.class).
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

                SignChestEquipStorage.removeSign(b);
            }
        }
    }
}
