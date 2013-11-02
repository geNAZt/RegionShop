package net.cubespace.RegionShop.Bukkit;

import net.cubespace.RegionShop.Data.Parser.ItemDB;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Table.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 25.10.13 21:54
 *
 * This class gets loaded by the Bukkit PluginManager
 */
public class Plugin extends JavaPlugin {
    /* This variable holds the Instance of the RegionShop which has been initialized */
    private static Plugin instance;

    /**
     * This function gets called when the Bukkit Plugin Manager wants to init the Plugin
     */
    public void onEnable() {
        instance = this;

        //Start the ItemDB
        new ItemDB();

        Player player = new Player();
        player.setName("skycrapper");

        try {
            Database.getDAO(Player.class).create(player);
            List<Player> player1 = Database.getDAO(Player.class).queryForEq("name", "skycrapper");

            for(Player player2 : player1) {
                System.out.println(player2.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function tells Bukkit to shut the Plugin down
     */
    public void shutdown() {
        setEnabled(false);
    }

    /**
     * @return Get the instance from the Bukkit Pluginloader
     */
    public static Plugin getInstance() {
        return instance;
    }
}
