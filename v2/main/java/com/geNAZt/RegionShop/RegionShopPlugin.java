package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Converter.ChestShopConverter;
import com.geNAZt.RegionShop.Data.Storage.Update;
import com.geNAZt.RegionShop.Data.Tasks.*;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Manager;
import com.geNAZt.RegionShop.Database.Table.*;
import com.geNAZt.RegionShop.Interface.Sign.CommandExecutor;
import com.geNAZt.RegionShop.Interface.Sign.Interact.Customer;
import com.geNAZt.RegionShop.Interface.Sign.Interact.Shop;
import com.geNAZt.RegionShop.Listener.*;
import com.geNAZt.RegionShop.Util.EssentialBridge;
import com.geNAZt.RegionShop.Util.Logger;
import com.geNAZt.RegionShop.Util.VaultBridge;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class RegionShopPlugin extends JavaPlugin {
    private static RegionShopPlugin instance;

    @Override
    public void onEnable() {
        //Logger first
        Logger.init(this);
        Logger.debug("===== Bootup RegionShop =====");
        instance = this;

        //Init Config
        Logger.debug("----- Loading Config -----");
        ConfigManager.init(this);
        VaultBridge.init(this);
        EssentialBridge.init(this);

        //Check for Update
        if(ConfigManager.main.Updater) {
            Updater updater = new Updater(this, 59082, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            Update.setUpdate(updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE);
            Update.setVersion(updater.getLatestName());
            Update.setLink(updater.getLatestFileLink());
        }

        //Database
        Logger.debug("----- Connecting Database -----");
        Manager manager = new Manager(this);

        manager.addModel(Player.class);
        manager.addModel(Region.class);
        manager.addModel(Items.class);
        manager.addModel(ItemMeta.class);
        manager.addModel(ItemMetaID.class, false);
        manager.addModel(ItemStorage.class);
        manager.addModel(Enchantment.class);
        manager.addModel(Transaction.class);
        manager.addModel(CustomerSign.class);
        manager.addModel(Chest.class);

        Database.setServer(manager.createDatabaseConnection());

        //Start all Tasks
        Logger.debug("----- Starting Tasks -----");

        for(Integer i = 0; i < ConfigManager.expert.Tasks_AsyncDatabaseWriters; i++) {
            getServer().getScheduler().runTaskAsynchronously(this, new AsyncDatabaseWriter());
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, new DetectWGChanges(this), 20, ConfigManager.expert.Timer_DetectWGChanges);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new PriceRecalculateTask(), 5 * 20, 5 * 20);
        getServer().getScheduler().runTaskLaterAsynchronously(this, new IntegrateServershop(), 100);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new SignOnChest(), ConfigManager.expert.Timer_DisplayItemTask, ConfigManager.expert.Timer_DisplayItemTask);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new CacheWarming(), 20, ConfigManager.expert.Timer_CacheWarmingTask);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new ShowcaseRefresh(), 5*20, 5*20);

        //Listener
        getServer().getPluginManager().registerEvents(new CheckForNewPlayer(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new DropEquip(), this);
        getServer().getPluginManager().registerEvents(new CommandExecutor(), this);
        getServer().getPluginManager().registerEvents(new SignInteractPrepare(), this);
        getServer().getPluginManager().registerEvents(new Customer(), this);
        getServer().getPluginManager().registerEvents(new Shop(), this);
        getServer().getPluginManager().registerEvents(new SignDestroy(), this);
        getServer().getPluginManager().registerEvents(new CheckChestProtection(), this);
        getServer().getPluginManager().registerEvents(new EquipOnChestClose(), this);
        getServer().getPluginManager().registerEvents(new PretendDisplaysToPickup(), this);
        getServer().getPluginManager().registerEvents(new RestoreOnChestOpen(), this);
        getServer().getPluginManager().registerEvents(new ShowcaseReloadOnChunkLoad(), this);

        //Shop Commands
        getCommand("shop").setExecutor(new com.geNAZt.RegionShop.Interface.CLI.CommandExecutor());

        //Maybe a converter ?
        if(ConfigManager.expert.Converter.equals("chestshop")) {
            new ChestShopConverter(this);
        }

        MCStats.init(this);
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
        if(!Database.getSaveQueue().isEmpty()) {
            while(!Database.getSaveQueue().isEmpty()) {
                Logger.info("Remaining Queue Size: " + Database.getSaveQueue().size());

                Object entity = Database.getSaveQueue().poll();
                Database.getServer().save(entity);
            }
        }

        if(!Database.getUpdateQueue().isEmpty()) {
            while(!Database.getUpdateQueue().isEmpty()) {
                Logger.info("Remaining Queue Size: " + Database.getUpdateQueue().size());

                Object entity = Database.getUpdateQueue().poll();
                Database.getServer().update(entity);
            }
        }

        //Log it
        getLogger().info("===== RegionShop Disabled =====");
    }

    public static synchronized RegionShopPlugin getInstance() {
        return instance;
    }
}
