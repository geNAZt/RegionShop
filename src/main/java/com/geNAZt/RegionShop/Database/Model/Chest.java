package com.geNAZt.RegionShop.Database.Model;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Table.ItemStorage;
import com.geNAZt.RegionShop.Database.Table.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 03.10.13
 */
public class Chest {
    //Insert a new Chest into the Database
    public static synchronized boolean store(org.bukkit.entity.Player player, Block sign, Block chest, World world) {
        //Check if Chest is stored
        if(isStored(chest, world)) {
            return false;
        }

        //Create new ItemStorage
        ItemStorage itemStorage = new ItemStorage();
        itemStorage.setName("c_" + chest.getX() +"_"+ chest.getY() +"_"+ chest.getZ());
        itemStorage.setSetting(ConfigManager.main.Group_defaultChestShop);

        //Create a new Chest
        com.geNAZt.RegionShop.Database.Table.Chest chest1 = new com.geNAZt.RegionShop.Database.Table.Chest();
        chest1.setChestX(chest.getX());
        chest1.setChestY(chest.getY());
        chest1.setChestZ(chest.getZ());
        chest1.setSignX(sign.getX());
        chest1.setSignY(sign.getY());
        chest1.setSignZ(sign.getZ());
        chest1.setWorld(world.getName());
        chest1.setName(player.getDisplayName() + "s ChestShop");
        chest1.setItemStorage(itemStorage);

        Database.getServer().save(itemStorage);
        Database.getServer().save(chest1);

        //Insert the owner
        return insertOwners(player, chest, world);
    }

    //Insert all Players who are owner of one Chest
    public static synchronized boolean insertOwners(org.bukkit.entity.Player player, Block chest, World world) {
        //Save all Players
        Player.insertNewPlayer(player);

        //Get the chest
        com.geNAZt.RegionShop.Database.Table.Chest chest1 = get(chest, world);

        //If the chest is not stored return false
        if(chest1 == null) {
            return false;
        }

        //Get a the list of users
        List<com.geNAZt.RegionShop.Database.Table.Player> players1 = new ArrayList<com.geNAZt.RegionShop.Database.Table.Player>();
        players1.add(Player.get(player));

        //Set the new Owner list
        chest1.setOwners(players1);

        //Update the Server
        Database.getServer().saveManyToManyAssociations(chest1, "owners");

        return true;
    }

    public static synchronized com.geNAZt.RegionShop.Database.Table.Chest get(Block chest, World world) {
        return get(chest, world, true);
    }

    //Get the stored region
    public static synchronized com.geNAZt.RegionShop.Database.Table.Chest get(Block chest, World world, Boolean caching) {
        return Database.getServer().find(com.geNAZt.RegionShop.Database.Table.Chest.class).
                    setUseCache(caching).
                    setUseQueryCache(caching).
                    where().
                        conjunction().
                            eq("chestX", chest.getX()).
                            eq("chestY", chest.getY()).
                            eq("chestZ", chest.getZ()).
                            eq("world", world.getName()).
                        endJunction().
                    findUnique();
    }

    //Check if Chest is already in the database
    public static synchronized boolean isStored(Block chest, World world) {
        return get(chest, world) != null;
    }

    public static void remove(com.geNAZt.RegionShop.Database.Table.Chest chest) {
        //Get all items
        Items item = chest.getItemStorage().getItems().iterator().next();

        //Get the chest
        Block chest1 = Bukkit.getWorld(chest.getWorld()).getBlockAt(chest.getChestX(), chest.getChestY(), chest.getChestZ());

        if(chest1.getType().equals(Material.CHEST)) {
            //Remove the ItemDrop above it
            for (Entity ent : Bukkit.getWorld(chest.getWorld()).getEntities()) {
                if(ent.getLocation().getBlockY() == chest1.getY()+1 && ent.getLocation().getBlockX() == chest1.getX() && ent.getLocation().getBlockZ() == chest1.getZ()) {
                    ent.remove();
                }
            }

            Database.getServer().delete(item);
            Database.getServer().delete(chest.getItemStorage());
            Database.getServer().delete(chest);
        } else {
            Database.getServer().delete(item);
            Database.getServer().delete(chest.getItemStorage());
            Database.getServer().delete(chest);
        }
    }
}
