package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;

import com.geNAZt.RegionShop.Command.Shop;

import com.geNAZt.RegionShop.Listener.PlayerDropItem;
import com.geNAZt.RegionShop.Listener.PlayerJoin;
import com.geNAZt.RegionShop.Listener.PlayerMove;
import com.geNAZt.RegionShop.Listener.PlayerQuit;

import com.geNAZt.RegionShop.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Model.ShopRegion;

import com.geNAZt.RegionShop.Storages.ListStorage;

import com.geNAZt.RegionShop.Util.Chat;

import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 19:27
 * To change this template use File | Settings | File Templates.
 */
public class RegionShopPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("[RegionShop] Enabled");

        //Database
        checkForDatabase();

        //Bridge init
        VaultBridge.init(this);
        WorldGuardBridge.init(this);
        EssentialBridge.init(this);

        //Storages
        ListStorage.init(this);

        //Utils
        Chat.init(this);

        //Listener
        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItem(this), this);

        //Commands
        getCommand("shop").setExecutor(new Shop(this));
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
        return list;
    }

    private void checkForDatabase() {
        try {
            getDatabase().find(ShopItems.class).findRowCount();
            getDatabase().find(ShopItemEnchantments.class).findRowCount();
            getDatabase().find(ShopRegion.class).findRowCount();
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
