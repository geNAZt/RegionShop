package com.geNAZt.RegionShop.Bukkit;

import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.geNAZt.RegionShop.Util.Loader.loadFromJAR;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.07.13
 */
public class ListenerManager {
    private static HashMap<Class, ArrayList<com.geNAZt.RegionShop.Listener.Listener>> listeners = new HashMap<Class, ArrayList<com.geNAZt.RegionShop.Listener.Listener>>();

    public ListenerManager(RegionShopPlugin plugin) {
        CopyOnWriteArrayList<Listener> bukkitListener = loadFromJAR("com.geNAZt.RegionShop.Bukkit.Listener", Listener.class);

        Logger.debug("Loaded Bukkit listener: " + bukkitListener.toString());

        for(Listener listener : bukkitListener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public static ArrayList<com.geNAZt.RegionShop.Listener.Listener> getHandler(Class event) {
        return listeners.get(event);
    }

    public static void addListener(Class event, com.geNAZt.RegionShop.Listener.Listener listener) {
        ArrayList<com.geNAZt.RegionShop.Listener.Listener> list = listeners.get(event);
        if(list == null) {
            list = new ArrayList<com.geNAZt.RegionShop.Listener.Listener>();
            list.add(listener);
            listeners.put(event, list);
            return;
        }

        list.add(listener);
    }
}
