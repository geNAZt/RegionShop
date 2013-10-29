package net.cubespace.RegionShop.Data.Storage;

import net.cubespace.RegionShop.Data.Struct.ItemDBEntry;
import net.cubespace.RegionShop.Util.Logger;

import java.util.HashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:46
 *
 * This Storage holds all Itemnames and Datanames for Items
 */
public class ItemDBStorage {
    private static HashMap<String, ItemDBEntry> itemDBEntries = new HashMap<String, ItemDBEntry>();
    private static Boolean enabled = false;

    /**
     * This function adds a new ItemDBEntry into the Storage
     *
     * @warning This will be filled by the Parser. Do not add ItemDBEntries by yourself
     * @param itemDBEntry The ItemDBEntry to add
     */
    public static void add(ItemDBEntry itemDBEntry) {
        itemDBEntries.put(itemDBEntry.getItemID() + ":" + itemDBEntry.getDataValue(), itemDBEntry);
    }

    /**
     * This function gets called if the Parser has read all lines.
     *
     * @warning This will be called by the Parser. Do not call it by yourself
     */
    public static void enable() {
        Logger.debug("ItemDBStorage - DB length: " + itemDBEntries.size());
        enabled = true;
    }

    /**
     * Lookup an Itemname in the ItemDB
     *
     * @param itemID The itemID which should be looked up
     * @param dataValue The dataValue which should be looked up
     * @return The Itemname with Datavalue prefix as String
     */
    public static String lookup(Integer itemID, Short dataValue) {
        //Check if storage is enabled
        if(!enabled) {
            Logger.warn("Tried to lookup an Item before the ItemDBStorage was ready. Request was: " + itemID + ":" + dataValue);
            return null;
        }

        //Check if the itemID:dataValue has a direct hit
        if(itemDBEntries.containsKey(itemID + ":" + dataValue)) {
            ItemDBEntry entry = itemDBEntries.get(itemID + ":" + dataValue);

            return entry.getDataName() + " " + entry.getItemName();
        }

        //Check if there is a 0 dataValue Entry
        if(itemDBEntries.containsKey(itemID + ":0")) {
            ItemDBEntry entry = itemDBEntries.get(itemID + ":0");

            return entry.getDataName() + " " + entry.getItemName();
        }

        //Nothing could be found (maybe new item or bukkitforge)
        Logger.warn("A item has been found which the ItemDB does not know. Please report an Issue with the ItemID and the Name of the Item");
        return null;
    }
}
