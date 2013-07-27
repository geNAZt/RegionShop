package com.geNAZt.RegionShop;

import com.avaje.ebean.EbeanServer;

import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;

import com.geNAZt.RegionShop.Bukkit.Events.SignInteract;
import com.geNAZt.RegionShop.Bukkit.ListenerManager;
import com.geNAZt.RegionShop.Bukkit.StaticManager;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Database.Manager;
import com.geNAZt.RegionShop.Database.Model.*;
import com.geNAZt.RegionShop.Interface.ShopExecutor;
import com.geNAZt.RegionShop.Listener.*;
import com.geNAZt.RegionShop.Listener.SignInteract.Customer;
import com.geNAZt.RegionShop.Listener.SignInteract.Equip;
import com.geNAZt.RegionShop.Listener.SignInteract.Sell;
import com.geNAZt.RegionShop.Updater.Updater;

import com.geNAZt.debugger.Profiler.Profiler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class RegionShopPlugin extends JavaPlugin implements Listener {
    private EbeanServer database;

    @Override
    public void onEnable() {
        com.geNAZt.debugger.Main.init(this);

        //Logger first
        Logger.init(this);

        Logger.debug("===== Bootup RegionShop =====");

        //Check for Updates
        Updater.init(this);
        Updater.check();

        //Init Config
        Logger.debug("----- Loading Config -----");
        getConfig().options().copyDefaults(true);
        saveConfig();

        //Database
        Logger.debug("----- Connecting Database -----");
        database = Manager.createDatabaseServer(this);
        checkForDatabase();

        //Start up Bukkit Wrappers
        Logger.debug("----- Bukkit API Wrapper -----");
        new StaticManager(this);
        new ListenerManager(this);

        //MCStats
        Logger.debug("----- Starting MCStats -----");
        MCStats.init(this);

        //Listeners
        Logger.debug("----- Appending Listeners -----");
        ListenerManager.addListener(PlayerJoinEvent.class, new PlayerJoin(this));
        ListenerManager.addListener(PlayerMoveEvent.class, new PlayerMove(this));
        ListenerManager.addListener(PlayerQuitEvent.class, new PlayerQuit());

        SignDestroy dss = new SignDestroy(this);
        ListenerManager.addListener(BlockBreakEvent.class, dss);
        ListenerManager.addListener(BlockPhysicsEvent.class, dss);

        ListenerManager.addListener(PlayerInteractEvent.class, new CheckChestProtection(this));
        ListenerManager.addListener(SignChangeEvent.class, new SignChange(this));
        ListenerManager.addListener(PlayerInteractEvent.class, new SignInteractPrepare());

        ListenerManager.addListener(PlayerDropItemEvent.class, new DropEquip());

        ListenerManager.addListener(SignInteract.class, new Sell(this));
        ListenerManager.addListener(SignInteract.class, new Equip(this));
        ListenerManager.addListener(SignInteract.class, new Customer(this));

        //ServerShop
        Profiler.start("ServerShop");
        if(getConfig().getBoolean("feature.servershop")) {
            File serverShop = new File(getDataFolder().getAbsolutePath(), "servershop");
            if(!serverShop.exists()) {
                boolean made = serverShop.mkdirs();
                if(!made) {
                    getLogger().warning("Could not create Servershop Config dir");
                } else {
                    InputStream stream = getResource("static/servershop/00-default.yml");
                    File fle = new File(serverShop.getAbsolutePath(), "00-default.yml");

                    try {
                        OutputStream oStream = new FileOutputStream(fle);

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = stream.read(buffer)) != -1) {
                            oStream.write(buffer, 0, len);
                        }

                        stream.close();
                        oStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            ServerShop.init(this);
        }

        Profiler.end("ServerShop");

        getCommand("shop").setExecutor(new ShopExecutor(this));

        Logger.info("===== RegionShop enabled =====");
    }

    public void disable() {
        setEnabled(false);
    }

    @Override
    public EbeanServer getDatabase() {
        return database;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(ShopItems.class);
        list.add(ShopItemEnchantments.class);
        list.add(ShopRegion.class);
        list.add(ShopChestEquipSign.class);
        list.add(ShopTransaction.class);
        list.add(ShopServerItemAverage.class);
        list.add(ShopBundle.class);
        list.add(ShopAddSign.class);
        list.add(ShopEquipSign.class);
        list.add(ShopCustomerSign.class);
        return list;
    }

    @Override
    protected void installDDL() {
        SpiEbeanServer serv = (SpiEbeanServer)getDatabase();
        DdlGenerator gen = serv.getDdlGenerator();

        String createSQL = gen.generateCreateDdl();
        getLogger().info(createSQL);

        gen.runScript(false, createSQL);
    }

    private void checkForDatabase() {
        try {
            getDatabase().find(ShopItems.class).findRowCount();
            getDatabase().find(ShopItemEnchantments.class).findRowCount();
            getDatabase().find(ShopRegion.class).findRowCount();
            getDatabase().find(ShopChestEquipSign.class).findRowCount();
            getDatabase().find(ShopTransaction.class).findRowCount();
            getDatabase().find(ShopServerItemAverage.class).findRowCount();
            getDatabase().find(ShopBundle.class).findRowCount();
            getDatabase().find(ShopAddSign.class).findRowCount();
            getDatabase().find(ShopEquipSign.class).findRowCount();
            getDatabase().find(ShopCustomerSign.class).findRowCount();

            getDatabase().runCacheWarming();
        } catch (PersistenceException ex) {
            getLogger().info("[RegionShop] Database hasn't setup.");
            installDDL();
        }
    }

    @Override
    public void onDisable() {
        saveConfig();

        getLogger().info("[RegionShop] Disabled");
    }
}
