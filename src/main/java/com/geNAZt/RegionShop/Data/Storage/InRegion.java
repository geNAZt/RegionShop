package com.geNAZt.RegionShop.Data.Storage;

import com.geNAZt.RegionShop.Database.Table.Region;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.09.13
 */
public class InRegion {
    private static HashMap<Player, Region> playersInRegions = new HashMap<Player, Region>();

    public static synchronized boolean has(Player player) {
        return playersInRegions.containsKey(player);
    }

    public static synchronized void put(Player player, Region region) {
        playersInRegions.put(player, region);
    }

    public static synchronized void remove(Player player) {
        playersInRegions.remove(player);
    }

    public static synchronized Region get(Player player) {
        return playersInRegions.get(player);
    }

    public static synchronized void remove(Region region) {
        for(Map.Entry<Player, Region> entry : playersInRegions.entrySet()) {
            if(entry.getValue().getLcName().equals(region.getLcName())) {
                playersInRegions.remove(entry.getKey());
            }
        }
    }
}
