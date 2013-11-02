package net.cubespace.RegionShop.Database.Repository;

import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Player;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last modified 02.11.2013, 16:04
 */
public class PlayerRepository {
    /**
     * Store a list of OfflinePlayer from Bukkit
     *
     * @param players List of Players to persist
     * @return A List of Booleans either the Player could be stored or not (the order is the same as the incoming List)
     */
    public static List<Boolean> insert(List<OfflinePlayer> players) {
        List<Boolean> returnList = new ArrayList<Boolean>();

        for(OfflinePlayer player : players) {
            returnList.add(insert(player));
        }

        return returnList;
    }

    /**
     * Get the stored Player via its OfflinePlayer from Bukkit
     *
     * @param player The OfflinePlayer to get
     * @return The Player from the Database
     */
    public static Player get(OfflinePlayer player) {
        try {
            List<Player> playerList = Database.getDAO(Player.class).queryForEq("name", player.getName().toLowerCase());
            if(playerList == null || playerList.size() == 0) {
                return null;
            }

            return playerList.get(0);
        } catch (SQLException e) {
            Logger.warn("Could not get the Player", e);
            return null;
        }
    }

    /**
     * Get the stored Players of all OfflinePlayers in the List
     *
     * @param players The list of OfflinePlayers to get
     * @return A List of Players which has been loaded from the Database
     */
    public static List<Player> get(List<OfflinePlayer> players) {
        List<Player> tablePlayers = new ArrayList<Player>();

        for(OfflinePlayer offlinePlayer : players) {
            tablePlayers.add(get(offlinePlayer));
        }

        return tablePlayers;
    }

    /**
     * Try to store a Player into the Database
     *
     * @param player The OfflinePlayer from Bukkit to store
     * @return True if the Player could get persisted / false if not
     */
    public static Boolean insert(OfflinePlayer player) {
        Player player1 = new Player();
        player1.setName(player.getName().toLowerCase());

        try {
            Database.getDAO(Player.class).create(player1);
            return true;
        } catch(SQLException e) {
            Logger.warn("Could not persist a Player", e);
            return false;
        }
    }

    /**
     * Check if a OfflinePlayer is in the Database
     *
     * @param player The OfflinePlayer to check
     * @return True if the Player is in the Database / false if not
     */
    public static Boolean isStored(OfflinePlayer player) {
        return get(player) != null;
    }
}
