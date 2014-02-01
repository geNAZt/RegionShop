package net.cubespace.RegionShop.Database.Repository;

import com.j256.ormlite.dao.ForeignCollection;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.PlayerMembersRegion;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsRegion;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class RegionRepository {
    //Check if user is inside a Region
    public static Region isIn(Player player) {
        Location playerLoc = player.getLocation();

        Region region = null;
        try {
            region = Database.getDAO(Region.class).queryBuilder().
                    where().
                    eq("world", playerLoc.getWorld().getName()).
                    and().
                    le("minX", playerLoc.getBlockX()).
                    and().
                    le("minY", playerLoc.getBlockY()).
                    and().
                    le("minZ", playerLoc.getBlockZ()).
                    and().
                    ge("maxX", playerLoc.getBlockX()).
                    and().
                    ge("maxY", playerLoc.getBlockY()).
                    and().
                    ge("maxZ", playerLoc.getBlockZ()).queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not query for Region", e);
        }

        return region;
    }

    //Update an existing Region
    public static boolean update(ProtectedRegion region, World world) {
        //Check if region is stored
        if (!isStored(region, world)) {
            return false;
        }

        Region region1 = get(region, world);

        //Update the position
        BlockVector min = region.getMinimumPoint();
        BlockVector max = region.getMaximumPoint();

        //Save min position
        region1.setMinX(min.getX());
        region1.setMinY(min.getY());
        region1.setMinZ(min.getZ());

        //Save max position
        region1.setMaxX(max.getX());
        region1.setMaxY(max.getY());
        region1.setMaxZ(max.getZ());

        try {
            Database.getDAO(Region.class).update(region1);
        } catch (SQLException e) {
            Logger.error("Could not update Region", e);
            return false;
        }

        //Store new Owners and members
        return insertOwners(region, world) && insertMembers(region, world);
    }

    //Insert a new Region into the Database
    public static boolean store(ProtectedRegion region, World world) {
        //Check if region is stored
        if (isStored(region, world)) {
            return false;
        }

        //Create new ItemStorage
        ItemStorage itemStorage = new ItemStorage();
        itemStorage.setName("r_" + region.getId().toLowerCase());
        itemStorage.setSetting(ConfigManager.groups.Group_defaultGroup);

        //Create a new Region
        Region region1 = new Region();
        region1.setName(region.getId().toLowerCase());
        region1.setLcName(region.getId().toLowerCase());
        region1.setRegion(region.getId());
        region1.setWorld(world.getName());
        region1.setItemStorage(itemStorage);

        BlockVector min = region.getMinimumPoint();
        BlockVector max = region.getMaximumPoint();

        //Save min position
        region1.setMinX(min.getX());
        region1.setMinY(min.getY());
        region1.setMinZ(min.getZ());

        //Save max position
        region1.setMaxX(max.getX());
        region1.setMaxY(max.getY());
        region1.setMaxZ(max.getZ());

        try {
            Database.getDAO(ItemStorage.class).create(itemStorage);
            Database.getDAO(Region.class).create(region1);
        } catch (SQLException e) {
            Logger.error("Could not save new Region", e);
            return false;
        }

        //Insert all owners
        return insertOwners(region, world) && insertMembers(region, world);
    }

    //Insert all Players who are members of one Region
    public static boolean insertMembers(ProtectedRegion region, World world) {
        //Convert the set into a OfflinePlayer List
        Iterator<String> owners = region.getMembers().getPlayers().iterator();
        ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        while (owners.hasNext()) {
            players.add(Bukkit.getOfflinePlayer(owners.next()));
        }

        //Save all Players
        PlayerRepository.insert(players);

        //Get the region
        Region region1 = get(region, world);

        //If the region is not stored return false
        if (region1 == null) {
            return false;
        }

        //Get a the list of users
        List<net.cubespace.RegionShop.Database.Table.Player> players1 = PlayerRepository.get(players);
        try {
            ForeignCollection<PlayerMembersRegion> playerForeignCollection = region1.getMembers();

            for(net.cubespace.RegionShop.Database.Table.Player player : players1) {
                PlayerMembersRegion playerMembersRegion = new PlayerMembersRegion();
                playerMembersRegion.setPlayer(player);
                playerMembersRegion.setRegion(region1);
                playerForeignCollection.add(playerMembersRegion);
            }

            //Set the new Member list
            region1.setMembers(playerForeignCollection);

            //Update the Server
            Database.getDAO(Region.class).update(region1);

            return true;
        } catch (SQLException e) {
            Logger.error("Could not save new Members in the Region", e);
            return false;
        }
    }

    //Insert all Players who are owner of one Region
    public static boolean insertOwners(ProtectedRegion region, World world) {
        //Convert the set into a OfflinePlayer List
        Iterator<String> owners = region.getOwners().getPlayers().iterator();
        ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        while (owners.hasNext()) {
            players.add(Bukkit.getOfflinePlayer(owners.next()));
        }

        //Save all Players
        PlayerRepository.insert(players);

        //Get the region
        Region region1 = get(region, world);

        //If the region is not stored return false
        if (region1 == null) {
            return false;
        }

        //Get a the list of users
        List<net.cubespace.RegionShop.Database.Table.Player> players1 = PlayerRepository.get(players);

        try {
            ForeignCollection<PlayerOwnsRegion> playerForeignCollection = region1.getOwners();

            for(net.cubespace.RegionShop.Database.Table.Player player : players1) {
                PlayerOwnsRegion playerOwnsRegion = new PlayerOwnsRegion();
                playerOwnsRegion.setPlayer(player);
                playerOwnsRegion.setRegion(region1);
                playerForeignCollection.add(playerOwnsRegion);
            }

            //Set the new Member list
            region1.setOwners(playerForeignCollection);

            //Update the Server
            Database.getDAO(Region.class).update(region1);

            return true;
        } catch (SQLException e) {
            Logger.error("Could not save new Owners in the Region", e);
            return false;
        }
    }

    //Remove the Region
    public static boolean remove(String region, World world) {
        //Check if Region is stored
        if (!isStored(region, world)) {
            return false;
        }

        //Get the region
        Region region1 = get(region, world);

        try {
            Database.getDAO(PlayerMembersRegion.class).delete(region1.getMembers());
            Database.getDAO(PlayerOwnsRegion.class).delete(region1.getOwners());
            Database.getDAO(Region.class).delete(region1);
            return true;
        } catch (SQLException e) {
            Logger.error("Could not remove Region", e);
            return false;
        }
    }

    //Get the stored region
    public static Region get(String region, World world) {
        try {
            return Database.getDAO(Region.class).queryBuilder().
                    where().
                    eq("region", region.toLowerCase()).
                    and().
                    eq("world", world.getName()).
                    queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not get Region", e);
            return null;
        }
    }

    //Get the stored region
    public static Region get(ProtectedRegion region, World world) {
        try {
            return Database.getDAO(Region.class).queryBuilder().
                    where().
                    eq("region", region.getId().toLowerCase()).
                    and().
                    eq("world", world.getName()).
                    queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not get Region", e);
            return null;
        }
    }

    //Check if Region is already in the database
    public static boolean isStored(String region, World world) {
        return get(region, world) != null;
    }

    //Check if Region is already in the database
    public static boolean isStored(ProtectedRegion region, World world) {
        return get(region, world) != null;
    }
}
