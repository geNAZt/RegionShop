package net.cubespace.RegionShop.Bukkit;

import net.cubespace.RegionShop.Data.Parser.ItemDB;
import net.cubespace.RegionShop.Util.Logger;
import net.cubespace.RegionShop.Util.Version;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
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

    private Integer amountOfOtherPlugins = 0;
    private Integer amoutOfEnabledPlugins = 0;

    /**
     * This function gets called when the Bukkit Plugin Manager wants to init the Plugin
     */
    public void onEnable() {
        instance = this;
        amountOfOtherPlugins = Bukkit.getPluginManager().getPlugins().length;

        Logger.info("Running on MC " + Version.getMcVersion());
        Logger.info("Delaying start of the Plugin so other Plugins have the chance to register");
        Logger.info("Detected " + amountOfOtherPlugins + " other Plugins. Waiting for them to enable");

        for(org.bukkit.plugin.Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if(plugin.isEnabled()) {
                amoutOfEnabledPlugins++;
            }
        }

        if(amountOfOtherPlugins.equals(amoutOfEnabledPlugins)) {
            Logger.info("Starting RegionShop");
            start();
        }
    }

    /**
     * Start the Plugin after all other Plugins have loaded
     */
    private void start() {
        new ItemDB();
    }

    /**
     * If a new Plugin gets enabled
     */
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        amoutOfEnabledPlugins++;

        if(amountOfOtherPlugins.equals(amoutOfEnabledPlugins)) {
            Logger.info("Starting RegionShop");
            start();
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
