package com.geNAZt.RegionShop.debugger.Profiler;

import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.debugger.Config;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.07.13
 */
public class ProfilerTask extends BukkitRunnable {
    @Override
    public void run() {
        for(Map.Entry<Long, HashMap<String, ArrayList<Long>>> entry : Profiler.profiles.entrySet()) {
            for(Map.Entry<String, ArrayList<Long>> entry1 : entry.getValue().entrySet()) {
                Long avg = 0L;

                for(Long grd : entry1.getValue()) {
                    avg += grd;
                }

                Double nsAvg = (avg/(double)(entry.getValue().size()));

                String threadStr = "MAIN";

                if(entry.getKey() != Thread.currentThread().getId()) {
                    threadStr = "ASYNC ID " + entry.getKey();
                }

                if(Config.getConfig().getBoolean("profiler.internal")) {
                    Logger.info("Profiler: " + entry1.getKey() + ": " + nsAvg / 100000 + " ms - " + threadStr);
                }
            }
        }


        Profiler.profiles = new ConcurrentHashMap<Long, HashMap<String, ArrayList<Long>>>();
    }
}
