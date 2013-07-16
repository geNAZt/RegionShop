package com.geNAZt.RegionShop.Data.Storages;

import com.geNAZt.RegionShop.Data.Struct.SignEquip;
import com.geNAZt.RegionShop.Database.Model.ShopEquipSign;
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
public class SignEquipStorage {
    private static HashMap<Block, SignEquip> signWorkers = new HashMap<Block, SignEquip>();
    private static RegionShopPlugin plugin;
    private static BukkitTask task;

    public static void init(RegionShopPlugin pl) {
        plugin = pl;

        List<ShopEquipSign> equipSigns = plugin.getDatabase().find(ShopEquipSign.class).findList();

        for(ShopEquipSign equipSign:equipSigns) {
            Block blk = plugin.getServer().getWorld(equipSign.getWorld()).getBlockAt(equipSign.getX(), equipSign.getY(), equipSign.getZ());
            addSign(blk, equipSign.getOwner(), equipSign.getShop(), equipSign.getWorld());
        }

        task = new com.geNAZt.RegionShop.Data.Tasks.SignEquip(plugin).runTaskTimer(plugin, 20, 60*20);
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
