package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.geNAZt.RegionShop.Command.ShopExecutor;

import com.geNAZt.RegionShop.Converter.ChestShopConverter;
import com.geNAZt.RegionShop.Events.RegionShopConfigReload;
import com.geNAZt.RegionShop.Listener.*;

import com.geNAZt.RegionShop.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Model.ShopRegion;

import com.geNAZt.RegionShop.Storages.ListStorage;

import com.geNAZt.RegionShop.Storages.SignEquipStorage;
import com.geNAZt.RegionShop.Util.Chat;

import com.geNAZt.RegionShop.Util.ItemConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
public class RegionShopPlugin extends JavaPlugin implements Listener {



    @Override
    public void onEnable() {
        getLogger().info("[RegionShop] Enabled");

        MCStats.init(this);

        //Database
        checkForDatabase();

        //Bridge init
        VaultBridge.init(this);
        WorldGuardBridge.init(this);
        EssentialBridge.init(this);

        //Storages
        ListStorage.init(this);

        if(getConfig().getBoolean("interfaces.sign.equip")) {
            SignEquipStorage.init(this);
        }

        //Utils
        Chat.init(this);
        ItemConverter.init(this);

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

            if(found == false) {
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
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(ShopItems.class);
        list.add(ShopItemEnchantments.class);
        list.add(ShopRegion.class);
        list.add(ShopEquipSign.class);
        return list;
    }

    private void checkForDatabase() {
        try {
            getDatabase().find(ShopItems.class).findRowCount();
            getDatabase().find(ShopItemEnchantments.class).findRowCount();
            getDatabase().find(ShopRegion.class).findRowCount();
            getDatabase().find(ShopEquipSign.class).findRowCount();
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
