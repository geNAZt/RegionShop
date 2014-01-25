package net.cubespace.RegionShop.Bukkit;

import net.cubespace.RegionShop.Bukkit.Listener.CheckForNewPlayer;
import net.cubespace.RegionShop.Data.Parser.ItemDB;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
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

        //Append the Listeners
        getServer().getPluginManager().registerEvents(new CheckForNewPlayer(), this);
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
