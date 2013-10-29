package net.cubespace.RegionShop.Data.Storage;

import net.cubespace.RegionShop.Data.Struct.ItemDBEntry;
import net.cubespace.RegionShop.Util.Logger;

import java.util.ArrayList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:46
 *
 * This Storage holds all Itemnames and Datanames for Items
 */
public class ItemDBStorage {
    private static ArrayList<ItemDBEntry> itemDBEntries = new ArrayList<ItemDBEntry>();

    /**
     * This function adds a new ItemDBEntry into the Storage
     *
     * @warning This will be filled by the Parser. Do not add ItemDBEntries by yourself
     * @param itemDBEntry The ItemDBEntry to add
     */
    public static void add(ItemDBEntry itemDBEntry) {
        itemDBEntries.add(itemDBEntry);

        Logger.debug("ItemDBStorage - Current DB length: " + itemDBEntries.size());
    }
}
