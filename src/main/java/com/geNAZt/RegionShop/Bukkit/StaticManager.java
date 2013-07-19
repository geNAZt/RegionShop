package com.geNAZt.RegionShop.Bukkit;

import com.geNAZt.RegionShop.Bukkit.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bukkit.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Resolver;
import com.geNAZt.RegionShop.Core.Add;
import com.geNAZt.RegionShop.Data.Storages.ListStorage;
import com.geNAZt.RegionShop.Data.Storages.Profiler;
import com.geNAZt.RegionShop.Data.Storages.SignChestEquipStorage;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.07.13
 */
public class StaticManager {
    public StaticManager(RegionShopPlugin plugin) {
        Profiler.start("InitBukkitStatic");

        //Start Core
        ItemConverter.init(plugin);
        Transaction.init(plugin);
        Add.init(plugin);

        //Utils
        Chat.init(plugin);
        Resolver.init(plugin);

        //Bridges
        EssentialBridge.init(plugin);
        VaultBridge.init(plugin);
        WorldGuardBridge.init(plugin);

        //Storages
        ListStorage.init(plugin);
        SignChestEquipStorage.init(plugin);

        Profiler.end("InitBukkitStatic");
    }
}
