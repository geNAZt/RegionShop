package com.geNAZt.RegionShop.Util;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 20:02
 * To change this template use File | Settings | File Templates.
 */
public class WorldGuardBridge {
    private WorldGuardBridge() {
        throw new AssertionError();
    }

    public static RegionManager getRegionManager(World wld) {
        return WGBukkit.getRegionManager(wld);
    }

    public static HashSet<ProtectedRegion> searchRegionsByOwner(String owner, Player p) {
        HashSet<ProtectedRegion> proRegionCollection = new HashSet<ProtectedRegion>();
        RegionManager rgMngr = getRegionManager(p.getWorld());
        Pattern r = Pattern.compile("(.*)regionshop(.*)");

        for( Map.Entry<String, ProtectedRegion> regionEntry : rgMngr.getRegions().entrySet()) {
            Matcher m = r.matcher(regionEntry.getKey());

            ProtectedRegion region = regionEntry.getValue();
            if (region.isOwner(owner) && region.getFlag(DefaultFlag.TELE_LOC) != null && m.matches()) {
                proRegionCollection.add(region);
            }
        }

        return proRegionCollection;
    }

    public static boolean isRegion(String region, Player p) {
        RegionManager rgMngr = getRegionManager(p.getWorld());

        return (rgMngr.getRegion(region) != null);
    }
}
