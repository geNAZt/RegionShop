package com.geNAZt.RegionShop.Storages;

import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.06.13
 */
public class SignStorage {
    private static ArrayList<BukkitTask> signWorkers = new ArrayList<BukkitTask>();
    protected static RegionShopPlugin plugin;

    private static class EquipTask extends BukkitRunnable {
        private Block sign;

        public EquipTask(Block sgn) {
            sign = sgn;
        }

        @Override
        public void run() {
            for(Integer y = -1; y<2; y++) {
                for(Integer x = -1; x<2; x++) {
                    for(Integer z = -1; z<2; z++) {
                        Block rl = sign.getRelative(x, y, z);

                        if (rl.getType().equals(Material.CHEST)) {
                            Chest chst = (Chest)rl.getState();
                            Inventory inv = chst.getInventory();

                            ItemStack[] chstContent = inv.getContents();

                            for(ItemStack iStack : chstContent) {
                                if(iStack == null) continue;

                                plugin.getLogger().info(iStack.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static void addSign(Block sign) {
        signWorkers.add(new EquipTask(sign).runTaskTimer(plugin, 20, 30*20));
    }
}
