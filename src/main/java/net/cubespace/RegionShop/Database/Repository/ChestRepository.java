package net.cubespace.RegionShop.Database.Repository;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsChest;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChestRepository {
    //Insert a new Chest into the Database
    public static boolean store(org.bukkit.entity.Player player, Block sign, Block chest, World world) {
        //Check if Chest is stored
        if (isStored(chest, world)) {
            return false;
        }

        //Create new ItemStorage
        ItemStorage itemStorage = new ItemStorage();
        itemStorage.setName("c_" + chest.getX() + "_" + chest.getY() + "_" + chest.getZ());
        itemStorage.setSetting(ConfigManager.groups.Group_defaultChestShop);

        //Create a new Chest
        net.cubespace.RegionShop.Database.Table.Chest chest1 = new net.cubespace.RegionShop.Database.Table.Chest();
        chest1.setChestX(chest.getX());
        chest1.setChestY(chest.getY());
        chest1.setChestZ(chest.getZ());
        chest1.setSignX(sign.getX());
        chest1.setSignY(sign.getY());
        chest1.setSignZ(sign.getZ());
        chest1.setWorld(world.getName());
        chest1.setName(player.getDisplayName() + "s ChestShop");
        chest1.setItemStorage(itemStorage);

        try {
            Database.getDAO(ItemStorage.class).create(itemStorage);
            Database.getDAO(Chest.class).create(chest1);
        } catch (SQLException e) {
            Logger.error("Could not create Chest", e);
            return false;
        }

        //Insert the owner
        return insertOwners(player, chest, world);
    }

    //Insert all Players who are owner of one Chest
    public static boolean insertOwners(org.bukkit.entity.Player player, Block chest, World world) {
        //Save all Players
        PlayerRepository.insert(player);

        //Get the chest
        net.cubespace.RegionShop.Database.Table.Chest chest1 = get(chest, world);

        //If the chest is not stored return false
        if (chest1 == null) {
            return false;
        }

        //Get a the list of users
        List<net.cubespace.RegionShop.Database.Table.Player> players1 = new ArrayList<net.cubespace.RegionShop.Database.Table.Player>();
        players1.add(PlayerRepository.get(player));

        try {
            ForeignCollection<PlayerOwnsChest> playerForeignCollection = chest1.getOwners();

            for (net.cubespace.RegionShop.Database.Table.Player playerT : players1) {
                PlayerOwnsChest playerOwnsRegion = new PlayerOwnsChest();
                playerOwnsRegion.setPlayer(playerT);
                playerOwnsRegion.setChest(chest1);
                playerForeignCollection.add(playerOwnsRegion);
            }

            //Set the new Member list
            chest1.setOwners(playerForeignCollection);

            //Update the Server
            Database.getDAO(Chest.class).update(chest1);

            return true;
        } catch (SQLException e) {
            Logger.error("Could not save new Owners in the Region", e);
            return false;
        }
    }

    public static net.cubespace.RegionShop.Database.Table.Chest get(Block chest, World world) {
        try {
            return Database.getDAO(Chest.class).queryBuilder().
                    where().
                    eq("chestX", chest.getX()).
                    and().
                    eq("chestY", chest.getY()).
                    and().
                    eq("chestZ", chest.getZ()).
                    and().
                    eq("world", world.getName()).
                    queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not get Chest", e);
            return null;
        }
    }

    public static net.cubespace.RegionShop.Database.Table.Chest getViaSign(Block chest, World world) {
        try {
            return Database.getDAO(Chest.class).queryBuilder().
                    where().
                    eq("signX", chest.getX()).
                    and().
                    eq("signY", chest.getY()).
                    and().
                    eq("signZ", chest.getZ()).
                    and().
                    eq("world", world.getName()).
                    queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not get Chest", e);
            return null;
        }
    }

    //Check if Chest is already in the database
    public static boolean isStored(Block chest, World world) {
        return get(chest, world) != null;
    }

    public static void remove(final net.cubespace.RegionShop.Database.Table.Chest chest) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                Block chest1 = Bukkit.getWorld(chest.getWorld()).getBlockAt(chest.getChestX(), chest.getChestY(), chest.getChestZ());

                if (chest1.getType().equals(Material.CHEST)) {
                    //Remove the ItemDrop above it
                    for (Entity ent : Bukkit.getWorld(chest.getWorld()).getEntities()) {
                        if (ent.getLocation().getBlockY() == chest1.getY() + 1 && ent.getLocation().getBlockX() == chest1.getX() && ent.getLocation().getBlockZ() == chest1.getZ()) {
                            ent.remove();
                        }
                    }
                }
            }
        });

        try {
            Database.getDAO(Items.class).delete(chest.getItemStorage().getItems());
            Database.getDAO(ItemStorage.class).delete(chest.getItemStorage());
            Database.getDAO(PlayerOwnsChest.class).delete(chest.getOwners());
            Database.getDAO(Chest.class).delete(chest);
        } catch (Exception e) {
            Logger.error("could not delete chest", e);
        }
    }
}
