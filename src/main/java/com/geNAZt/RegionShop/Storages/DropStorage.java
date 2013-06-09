package com.geNAZt.RegionShop.Storages;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class DropStorage {
    private static final HashMap<Player, String> playersDropTo = new HashMap<Player, String>();

    public static String getPlayer(Player plyr) {
        if (!playersDropTo.containsKey(plyr)) {
            return null;
        }

        return playersDropTo.get(plyr);
    }

    public static void setPlayer(Player plyr, String region) {
        playersDropTo.put(plyr, region);
    }

    public static void removerPlayer(Player plyr) {
        playersDropTo.remove(plyr);
    }
}
