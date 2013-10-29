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
}
