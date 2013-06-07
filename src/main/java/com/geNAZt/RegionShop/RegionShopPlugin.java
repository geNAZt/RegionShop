package com.geNAZt.RegionShop;

import com.geNAZt.RegionShop.Command.Shop;
import com.geNAZt.RegionShop.Event.LeaveEvent;
import com.geNAZt.RegionShop.Event.RegionEntered;
import com.geNAZt.RegionShop.Model.ShopItems;
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

        //Event
        getServer().getPluginManager().registerEvents(new RegionEntered(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);

        //Commands
        getCommand("shop").setExecutor(new Shop(this));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(ShopItems.class);
        return list;
    }

    private void checkForDatabase() {
        try {
            getDatabase().find(ShopItems.class).findRowCount();
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
