package com.geNAZt.RegionShop.Database.Model;

import com.geNAZt.RegionShop.Database.Database;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 05.09.13
 */
public class Player {
    //Store a list of Users
    public static void insertNewPlayers(List<OfflinePlayer> players) {
        for(OfflinePlayer player : players) {
            insertNewPlayer(player);
        }
    }

    //Get one User
    public static com.geNAZt.RegionShop.Database.Table.Player get(OfflinePlayer player) {
        return Database.getServer().
                    find(com.geNAZt.RegionShop.Database.Table.Player.class).
                    where().
                        eq("name", player.getName().toLowerCase()).
                    findUnique();
    }

    //Get a list of user
    public static List<com.geNAZt.RegionShop.Database.Table.Player> get(List<OfflinePlayer> players) {
        ArrayList<com.geNAZt.RegionShop.Database.Table.Player> tablePlayers = new ArrayList<com.geNAZt.RegionShop.Database.Table.Player>();

        for(OfflinePlayer offlinePlayer : players) {
            tablePlayers.add(get(offlinePlayer));
        }

        return tablePlayers;
    }

    //Store a new User in the Database
    public static void insertNewPlayer(OfflinePlayer player) {
        if(isStored(player)) return;

        com.geNAZt.RegionShop.Database.Table.Player player1 = new com.geNAZt.RegionShop.Database.Table.Player();
        player1.setName(player.getName().toLowerCase());

        Database.getServer().save(player1);
    }

    //Check if a User is inside the database
    public static boolean isStored(OfflinePlayer player) {
        return get(player) != null;
    }
}
