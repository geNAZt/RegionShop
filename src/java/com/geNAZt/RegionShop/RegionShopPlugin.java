package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Tasks.AsyncDatabaseWriter;
import com.geNAZt.RegionShop.Data.Tasks.DetectWGChanges;
import com.geNAZt.RegionShop.Database.Database;

import com.geNAZt.RegionShop.Database.Manager;
import com.geNAZt.RegionShop.Database.Model.*;
import com.geNAZt.RegionShop.Listener.CheckForNewPlayer;
import com.geNAZt.RegionShop.Listener.WGChanges;
import com.geNAZt.RegionShop.Util.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class RegionShopPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        //Logger first
        Logger.init(this);
        Logger.debug("===== Bootup RegionShop =====");

        //Init Config
        Logger.debug("----- Loading Config -----");
        ConfigManager.init(this);

        //Database
        Logger.debug("----- Connecting Database -----");
        Manager manager = new Manager(this);

        manager.addModel(Player.class);
        manager.addModel(Region.class);
        manager.addModel(Items.class);
        manager.addModel(ItemMeta.class);
        manager.addModel(ItemMetaID.class, false);

        Database.setServer(manager.createDatabaseConnection());

        //Start all Tasks
        Logger.debug("----- Starting Tasks -----");

        for(Integer i = 0; i < ConfigManager.timings.AsyncDatabaseWriters; i++) {
            getServer().getScheduler().runTaskAsynchronously(this, new AsyncDatabaseWriter());
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, new DetectWGChanges(this), 20, ConfigManager.timings.DetectWGChanges);

        //Listener
        getServer().getPluginManager().registerEvents(new CheckForNewPlayer(), this);
        getServer().getPluginManager().registerEvents(new WGChanges(), this);

        //Shop Commands
        //getCommand("shop").setExecutor(new ShopExecutor(this));

        Logger.info("===== RegionShop enabled =====");
    }

    public void disable() {
        setEnabled(false);
    }

    @Override
    public void onDisable() {
        //Close all threads
        getServer().getScheduler().cancelTasks(this);

        //Disable the Plugin in the Manager
        getServer().getPluginManager().disablePlugin(this);

        //Log it
        getLogger().info("===== RegionShop Disabled =====");
    }
}
