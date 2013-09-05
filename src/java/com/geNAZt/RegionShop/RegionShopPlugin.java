package com.geNAZt.RegionShop;

import com.avaje.ebean.bean.EntityBean;
import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Tasks.AsyncDatabaseWriter;
import com.geNAZt.RegionShop.Data.Tasks.DetectWGChanges;
import com.geNAZt.RegionShop.Database.Database;

import com.geNAZt.RegionShop.Database.Manager;
import com.geNAZt.RegionShop.Database.Table.*;
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

        for(Integer i = 0; i < ConfigManager.expert.Tasks_AsyncDatabaseWriters; i++) {
            getServer().getScheduler().runTaskAsynchronously(this, new AsyncDatabaseWriter());
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, new DetectWGChanges(this), 20, ConfigManager.expert.Timer_DetectWGChanges);

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
        Logger.info("===== Disabling RegionShop =====");

        //Close all threads
        Logger.info("----- Stopping all Tasks -----");
        getServer().getScheduler().cancelTasks(this);

        //Check if the Database has flushed
        Logger.info("----- Shutting down Database Connection -----");
        if(!Database.getQueue().isEmpty()) {
            //It has not completly flushed. Get all SQL Statements and save them to files
            while(!Database.getQueue().isEmpty()) {
                Logger.info("Remaining Queue Size: " + Database.getQueue().size());

                Object entity = Database.getQueue().poll();
                Database.getServer().save(entity);
            }
        }

        //Log it
        getLogger().info("===== RegionShop Disabled =====");
    }
}
