package com.geNAZt.RegionShop.Database;

import com.avaje.ebean.EbeanServer;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 31.08.13
 */
public class Database {
    //Store the Database instance
    private static EbeanServer server;
    //A new and empty ArrayQueue *blingbling*
    private static ArrayBlockingQueue<Object> saveQueue = new ArrayBlockingQueue<Object>(5000);
    private static ArrayBlockingQueue<Object> updateQueue = new ArrayBlockingQueue<Object>(5000);

    //Set the Database instance to a valid Conneciton
    public static void setServer(EbeanServer server1) {
        server = server1;
    }

    //Get the Database Server Connection
    public static EbeanServer getServer() {
        return server;
    }

    //Get the DatabaseQueue which handles all writes in an async way
    public static synchronized ArrayBlockingQueue<Object> getSaveQueue() {
        return saveQueue;
    }

    //Get the DatabaseQueue which handles all Updates in an async way
    public static synchronized ArrayBlockingQueue<Object> getUpdateQueue() {
        return updateQueue;
    }
}
