package com.geNAZt.RegionShop.Data.Tasks;

import com.avaje.ebean.EbeanServer;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Util.Logger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class AsyncDatabaseWriter extends BukkitRunnable {
    @Override
    public void run() {
        //Get the Queue
        ArrayBlockingQueue<Object> saves = Database.getSaveQueue();
        ArrayBlockingQueue<Object> updates = Database.getUpdateQueue();

        //Get the Database Connection
        EbeanServer db = Database.getServer();

        //Run dis foreva
        while(true) {
            try {
                Object obj = saves.poll(10, TimeUnit.MILLISECONDS);
                if(obj != null) db.save(obj);

                obj = updates.poll(10, TimeUnit.MILLISECONDS);
                if(obj != null) db.update(obj);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logger.fatal("Got fatal error in AsyncDBWriter");
            }
        }
    }
}
