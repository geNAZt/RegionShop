package com.geNAZt.RegionShop.Data.Storages;

import com.geNAZt.RegionShop.Data.Struct.SignEquip;
import com.geNAZt.RegionShop.Data.Tasks.SignChestEquip;
import com.geNAZt.RegionShop.Database.Database.Model.ShopChestEquipSign;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.06.13
 */
public class SignChestEquipStorage {
    private static HashMap<Block, SignEquip> signWorkers = new HashMap<Block, SignEquip>();
    private static BukkitTask task;

    public static void init(RegionShopPlugin plugin) {
        List<ShopChestEquipSign> equipSigns = plugin.getDatabase().find(ShopChestEquipSign.class).findList();

        for(ShopChestEquipSign equipSign:equipSigns) {
            Block blk = plugin.getServer().getWorld(equipSign.getWorld()).getBlockAt(equipSign.getX(), equipSign.getY(), equipSign.getZ());
            addSign(blk, equipSign.getOwner(), equipSign.getShop(), equipSign.getWorld());
        }

        task = new SignChestEquip(plugin).runTaskTimer(plugin, 20, 60*20);
    }

    public static void addSign(Block sign, String owner, String region, String world) {
        signWorkers.put(sign, new SignEquip(owner, region, world));
    }

    public static void removeSign(Block sign) {
        signWorkers.remove(sign);
    }

    public static HashMap<Block, SignEquip> getAll() {
        return signWorkers;
    }

    public static void unload() {
        signWorkers = new HashMap<Block, SignEquip>();
        task.cancel();
    }

    public static int getTotalCount() {
        return signWorkers.size();
    }
}
