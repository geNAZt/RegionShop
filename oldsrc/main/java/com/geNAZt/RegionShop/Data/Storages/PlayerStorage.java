package com.geNAZt.RegionShop.Data.Storages;

import com.geNAZt.RegionShop.Data.Struct.Region;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class PlayerStorage {
    private static final HashMap<Player, Region> playersInRegions = new HashMap<Player, Region>();

    public static Region get(Player plyr) {
        if (!playersInRegions.containsKey(plyr)) {
            return null;
        }

        return playersInRegions.get(plyr);
    }

    public static boolean has(Player player) {
        return playersInRegions.containsKey(player);
    }

    public static void set(Player plyr, Region region) {
        playersInRegions.put(plyr, region);
    }

    public static void remove(Player plyr) {
        playersInRegions.remove(plyr);
    }
}
