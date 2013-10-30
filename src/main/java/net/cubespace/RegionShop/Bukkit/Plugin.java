package net.cubespace.RegionShop.Bukkit;

import net.cubespace.RegionShop.Data.Parser.ItemDB;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 25.10.13 21:54
 *
 * This class gets loaded by the Bukkit PluginManager
 */
public class Plugin extends JavaPlugin implements Listener {
    /* This variable holds the Instance of the RegionShop which has been initialized */
    private static Plugin instance;

    /**
     * This function gets called when the Bukkit Plugin Manager wants to init the Plugin
     */
    public void onEnable() {
        instance = this;

        //Start the ItemDB
        new ItemDB();
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
