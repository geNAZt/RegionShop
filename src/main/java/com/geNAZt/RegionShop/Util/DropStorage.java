package com.geNAZt.RegionShop.Util;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class DropStorage {
    private static HashMap<Player, String> playersDropTo = new HashMap<Player, String>();

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
