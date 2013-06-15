package com.geNAZt.RegionShop.Converter.ChestShop;

import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class PlayerResetTask extends BukkitRunnable {
    private Player plyr = null;

    public PlayerResetTask(Player player) {
        plyr = player;
    }

    public void run() {
        ArrayList<Integer> aList = new ArrayList<Integer>();
        aList.add(-1);
        aList.add(-1);

        if(ConvertStorage.hasPlayer(plyr)) {
            ConvertStorage.setPlayer(plyr, aList);
            plyr.sendMessage(Chat.getPrefix() + "Your convert status has been reseted");
        }
    }
}
