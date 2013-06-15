package com.geNAZt.RegionShop.Converter.ChestShop;

import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class ConvertStorage {
    private static RegionShopPlugin plugin = null;
    private static final HashMap<Player, ArrayList<Integer>> playersInConvert = new HashMap<Player, ArrayList<Integer>>();
    private static final HashMap<Player, BukkitTask> playerReset = new HashMap<Player, BukkitTask>();

    public static void init(RegionShopPlugin pl) {
        plugin = pl;
    }

    public static boolean hasPlayer(Player plyr) {
        if (!playersInConvert.containsKey(plyr)) {
            return false;
        }

        return true;
    }

    public static void setPlayer(Player plyr, ArrayList<Integer> map) {
        if(map.get(0) != -1 || map.get(1) != -1) {
            if(!playerReset.containsKey(plyr)) {
                playerReset.put(plyr, new PlayerResetTask(plyr).runTaskLater(plugin, 40));
            }
        }

        playersInConvert.put(plyr, map);
    }

    public static ArrayList<Integer> getPlayer(Player player) {
        return playersInConvert.get(player);
    }

    public static void removerPlayer(Player plyr) {
        if(playerReset.containsKey(plyr)) {
            BukkitTask task = playerReset.get(plyr);
            task.cancel();

            playerReset.remove(plyr);
        }

        playersInConvert.remove(plyr);
    }
}
