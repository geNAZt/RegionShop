package com.geNAZt.RegionShop;

import com.avaje.ebean.EbeanServer;
import com.geNAZt.RegionShop.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Converter.ChestShopConverter;
import com.geNAZt.RegionShop.Events.RegionShopConfigReload;
import com.geNAZt.RegionShop.Interface.ShopExecutor;
import com.geNAZt.RegionShop.Listener.*;
import com.geNAZt.RegionShop.Model.*;
import com.geNAZt.RegionShop.Region.Resolver;
import com.geNAZt.RegionShop.ServerShop.ServerShop;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Storages.SignEquipStorage;
import com.geNAZt.RegionShop.Transaction.Transaction;
import com.geNAZt.RegionShop.Updater.Updater;
import com.geNAZt.RegionShop.Util.AdminTeller;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class RegionShopPlugin extends JavaPlugin implements Listener {
    private EbeanServer database;

    @Override
    public void onEnable() {
        getLogger().info("[RegionShop] Enabled");

        Updater.init(this);

        //Check if Version has changed
        File versionFile = new File(getDataFolder().getAbsolutePath(), "version");
        if(versionFile.exists()) {
            try {
                StringBuilder fileContents = new StringBuilder((int)versionFile.length());
                Scanner scanner = new Scanner(versionFile);

                try {
                    while(scanner.hasNextLine()) {
                        fileContents.append(scanner.nextLine());
                    }

                    String buildNumber = fileContents.toString();
                    Integer build;

                    try {
                        build = Integer.parseInt(buildNumber);

                        getLogger().info("Build Number: " + build);

                        if(build < Updater.getCurrentBuild()) {
                            //Needs updates
                            Updater.update(build);
                        }
                    } catch(NumberFormatException e) {
                        e.printStackTrace();
                    }

                } finally {
                    scanner.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                OutputStream oStream = new FileOutputStream(versionFile);
                oStream.write(String.valueOf(Updater.getCurrentBuild()).getBytes());
                oStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MCStats.init(this);

        //Config
        getConfig().options().copyDefaults(true);
        saveConfig();

        //Database
        database = Database.createDatabaseServer(this);
        checkForDatabase();

        //Bridge init
        VaultBridge.init(this);
        WorldGuardBridge.init(this);
        EssentialBridge.init(this);
        Resolver.init(this);

        //Storages
        ListStorage.init(this);

        if(getConfig().getBoolean("interfaces.sign.equip")) {
            SignEquipStorage.init(this);
        }

        //Utils
        Chat.init(this);
        ItemConverter.init(this);
        AdminTeller.init(this);

        //Transaction
        new Transaction(this);

        //Listener
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);


        getServer().getPluginManager().registerEvents(new SignChange(this), this);
        getServer().getPluginManager().registerEvents(new BlockDestroy(this), this);


        if(getConfig().getBoolean("interfaces.command.equip")) getServer().getPluginManager().registerEvents(new PlayerDropItem(this), this);

        //Commands
        getCommand("shop").setExecutor(new ShopExecutor(this));

        //Converter
        if(getConfig().getBoolean("converter.chestshop")) new ChestShopConverter(this);

        //Server Shop
        if(getConfig().getBoolean("feature.servershop")) {
            File serverShop = new File(getDataFolder().getAbsolutePath(), "servershop");
            if(!serverShop.exists()) {
                boolean made = serverShop.mkdirs();
                if(!made) {
                    getLogger().warning("Could not create Servershop Config dir");
                }

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

            ServerShop.init(this);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onConfigReload(RegionShopConfigReload cnfrld) {
        if(!getConfig().getBoolean("interfaces.command.equip")) {
            for(RegisteredListener listener : PlayerDropItemEvent.getHandlerList().getRegisteredListeners()) {
                if(listener.getListener().toString().contains("com.geNAZt.RegionShop.Listener.PlayerDropItem")) {
                    PlayerDropItemEvent.getHandlerList().unregister(listener);
                    getLogger().info("Removed com.geNAZt.RegionShop.Listener.PlayerDropItem Listener");
                    break;
                }
            }
        } else {
            boolean found = false;

            for(RegisteredListener listener : PlayerDropItemEvent.getHandlerList().getRegisteredListeners()) {
                if(listener.getListener().toString().contains("com.geNAZt.RegionShop.Listener.PlayerDropItem")) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                getServer().getPluginManager().registerEvents(new PlayerDropItem(this), this);
                getLogger().info("Added com.geNAZt.RegionShop.Listener.PlayerDropItem Listener");
            }
        }

        if(!getConfig().getBoolean("interfaces.sign.equip")) {
            SignEquipStorage.unload();
            getLogger().info("Unloaded SignEquipStorage");
        } else {
            if(SignEquipStorage.getTotalCount() < 1) {
                SignEquipStorage.init(this);
                getLogger().info("Loaded SignEquipStorage");
            }
        }
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
        list.add(ShopEquipSign.class);
        list.add(ShopTransaction.class);
        list.add(ShopServerItemAverage.class);
        list.add(ShopBundle.class);
        return list;
    }

    private void checkForDatabase() {
        try {
            getDatabase().find(ShopItems.class).findRowCount();
            getDatabase().find(ShopItemEnchantments.class).findRowCount();
            getDatabase().find(ShopRegion.class).findRowCount();
            getDatabase().find(ShopEquipSign.class).findRowCount();
            getDatabase().find(ShopTransaction.class).findRowCount();
            getDatabase().find(ShopServerItemAverage.class).findRowCount();
            getDatabase().find(ShopBundle.class).findRowCount();

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
