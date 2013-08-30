package com.geNAZt.RegionShop.debugger.Profiler;

import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.RegionShopPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.07.13
 */
public class Profiler {
    public static ConcurrentHashMap<Long, HashMap<String, ArrayList<Long>>> profiles = new ConcurrentHashMap<Long, HashMap<String, ArrayList<Long>>>();
    private static final ConcurrentHashMap<Long, HashMap<String, Long>> start = new ConcurrentHashMap<Long, HashMap<String, Long>>();
    private static Boolean enabled = false;

    public static void init(RegionShopPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, new ProfilerTask(), 20, 20);

        enabled = true;
    }

    public synchronized static void start(String name) {
        if(enabled) {
            long tID = Thread.currentThread().getId();

            if(start.get(tID) != null) {
                HashMap<String, Long> prof = start.get(tID);
                if(prof.get(name) == null) {
                    prof.put(name, System.nanoTime());
                } else {
                    Logger.warn("Profiler: Tryed to start a Timer which is already started: " + name);
                }
            } else {
                HashMap<String, Long> prof = new HashMap<String, Long>();
                prof.put(name, System.nanoTime());

                start.put(tID, prof);
            }
        }
    }

    public synchronized static void end(String name) {
        if(enabled) {
            long tID = Thread.currentThread().getId();
            HashMap<String, Long> prof = start.get(tID);

            if(prof != null) {
                Long startTime = prof.get(name);
                if(startTime != null) {
                    HashMap<String, ArrayList<Long>> list = profiles.get(tID);

                    if(list == null) {
                        ArrayList<Long> alist = new ArrayList<Long>();
                        alist.add(System.nanoTime() - startTime);

                        list = new HashMap<String, ArrayList<Long>>();
                        list.put(name, alist);

                        profiles.put(tID, list);
                    } else {
                        ArrayList<Long> alist = list.get(name);

                        if(alist == null) {
                            alist = new ArrayList<Long>();
                            alist.add(System.nanoTime() - startTime);
                            list.put(name, alist);
                        } else {
                            alist.add(System.nanoTime() - startTime);
                        }
                    }

                    prof.remove(name);
                } else {
                    Logger.warn("Profiler: Tryed to end a Timer which is not started: " + name);
                }
            }
        }
    }
}
