package com.geNAZt.RegionShop.Data.Storage;

import com.geNAZt.RegionShop.Database.Table.Region;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Drop {
    private static final HashMap<Player, Region> playersDropTo = new HashMap<Player, Region>();

    public static Region get(Player plyr) {
        if (!playersDropTo.containsKey(plyr)) {
            return null;
        }

        return playersDropTo.get(plyr);
    }

    public static boolean has(Player player) {
        return playersDropTo.containsKey(player);
    }

    public static void put(Player plyr, Region region) {
        playersDropTo.put(plyr, region);
    }

    public static void remove(Player plyr) {
        playersDropTo.remove(plyr);
    }
}
