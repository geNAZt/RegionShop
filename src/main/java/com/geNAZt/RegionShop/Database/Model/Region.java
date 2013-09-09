package com.geNAZt.RegionShop.Database.Model;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 05.09.13
 */
public class Region {
    //Update an existing Region
    public static synchronized boolean update(ProtectedRegion region, World world) {
        //Check if region is stored
        if(!isStored(region, world)) {
            return false;
        }

        com.geNAZt.RegionShop.Database.Table.Region region1 = get(region, world);

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

        Database.getServer().update(region1);

        //Store new Owners and members
        return insertOwners(region, world) && insertMembers(region, world);
    }

    //Insert a new Region into the Database
    public static synchronized boolean store(ProtectedRegion region, World world) {
        //Check if region is stored
        if(isStored(region, world)) {
            return false;
        }

        //Create a new Region
        com.geNAZt.RegionShop.Database.Table.Region region1 = new com.geNAZt.RegionShop.Database.Table.Region();
        region1.setBundle(false);
        region1.setCurrentGroup(ConfigManager.main.Group_defaultGroup);
        region1.setName(region.getId());
        region1.setRegion(region.getId());
        region1.setWorld(world.getName());

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

        Database.getServer().save(region1);


        //Insert all owners
        return insertOwners(region, world) && insertMembers(region, world);
    }

    //Insert all Players who are members of one Region
    public static synchronized boolean insertMembers(ProtectedRegion region, World world) {
        //Convert the set into a OfflinePlayer List
        Iterator<String> owners = region.getMembers().getPlayers().iterator();
        ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        while(owners.hasNext()) {
            players.add(Bukkit.getOfflinePlayer(owners.next()));
        }

        //Save all Players
        Player.insertNewPlayers(players);

        //Get the region
        com.geNAZt.RegionShop.Database.Table.Region region1 = get(region, world);

        //If the region is not stored return false
        if(region1 == null) {
            return false;
        }

        //Get a the list of users
        List<com.geNAZt.RegionShop.Database.Table.Player> players1 = Player.get(players);

        //Set the new Owner list
        region1.setMembers(players1);

        //Update the Server
        Database.getServer().saveManyToManyAssociations(region1, "members");

        return true;
    }

    //Insert all Players who are owner of one Region
    public static synchronized boolean insertOwners(ProtectedRegion region, World world) {
        //Convert the set into a OfflinePlayer List
        Iterator<String> owners = region.getOwners().getPlayers().iterator();
        ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();

        while(owners.hasNext()) {
            players.add(Bukkit.getOfflinePlayer(owners.next()));
        }

        //Save all Players
        Player.insertNewPlayers(players);

        //Get the region
        com.geNAZt.RegionShop.Database.Table.Region region1 = get(region, world);

        //If the region is not stored return false
        if(region1 == null) {
            return false;
        }

        //Get a the list of users
        List<com.geNAZt.RegionShop.Database.Table.Player> players1 = Player.get(players);

        //Set the new Owner list
        region1.setOwners(players1);

        //Update the Server
        Database.getServer().saveManyToManyAssociations(region1, "owners");

        return true;
    }

    //Remove the Region
    public static synchronized boolean remove(String region, World world) {
        //Check if Region is stored
        if(!isStored(region, world)) {
            return false;
        }

        //Get the region
        com.geNAZt.RegionShop.Database.Table.Region region1 = get(region, world);

        Database.getServer().deleteManyToManyAssociations(region1, "owners");
        Database.getServer().deleteManyToManyAssociations(region1, "members");
        Database.getServer().delete(region1);

        return true;
    }

    //Get the stored region
    public static synchronized com.geNAZt.RegionShop.Database.Table.Region get(String region, World world) {
        return Database.getServer().find(com.geNAZt.RegionShop.Database.Table.Region.class).
                    where().
                        eq("region", region).
                        eq("world", world.getName()).
                    findUnique();
    }

    //Get the stored region
    public static synchronized com.geNAZt.RegionShop.Database.Table.Region get(ProtectedRegion region, World world) {
        return Database.getServer().find(com.geNAZt.RegionShop.Database.Table.Region.class).
                    where().
                        eq("region", region.getId()).
                        eq("world", world.getName()).
                    findUnique();
    }

    //Check if Region is already in the database
    public static synchronized boolean isStored(String region, World world) {
        return get(region, world) != null;
    }

    //Check if Region is already in the database
    public static synchronized boolean isStored(ProtectedRegion region, World world) {
        return get(region, world) != null;
    }
}
