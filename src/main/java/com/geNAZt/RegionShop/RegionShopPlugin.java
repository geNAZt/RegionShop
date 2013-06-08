package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Command.Shop;
import com.geNAZt.RegionShop.Event.DropItemEvent;
import com.geNAZt.RegionShop.Event.JoinEvent;
import com.geNAZt.RegionShop.Event.LeaveEvent;
import com.geNAZt.RegionShop.Event.RegionEntered;
import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.EssentialBridge;
import com.geNAZt.RegionShop.Util.VaultBridge;

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

        //Check for economy plugin
        if (!VaultBridge.setupEconomy(this)) {
            getLogger().warning("No Economy Plugin found.");
            setEnabled(false);
        }

        //Database
        checkForDatabase();

        //Statics init
        Chat.init(this);
        EssentialBridge.init(this);

        //Event
        getServer().getPluginManager().registerEvents(new RegionEntered(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new DropItemEvent(this), this);

        //Commands
        getCommand("shop").setExecutor(new Shop(this));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(ShopItems.class);
        list.add(ShopItemEnchantmens.class);
        return list;
    }

    private void checkForDatabase() {
        try {
            getDatabase().find(ShopItems.class).findRowCount();
            getDatabase().find(ShopItemEnchantmens.class).findRowCount();
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
