package com.geNAZt.RegionShop.Database.Model;

import com.geNAZt.RegionShop.Database.Database;
import org.bukkit.OfflinePlayer;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 05.09.13
 */
public class Player {
    //Store a new User in the Database
    public static void insertNewPlayer(OfflinePlayer player) {
        com.geNAZt.RegionShop.Database.Table.Player player1 = new com.geNAZt.RegionShop.Database.Table.Player();
        player1.setName(player.getName());

        Database.getQueue().add(player1);
    }

    //Check if a User is inside the database
    public static boolean isStored(OfflinePlayer player) {
        return Database.getServer().
                find(com.geNAZt.RegionShop.Database.Table.Player.class).
                    where().
                        eq("name", player.getName()).
                findUnique() != null;

    }
}
