package net.cubespace.RegionShop.Data.Parser;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Data.Storage.ItemDBStorage;
import net.cubespace.RegionShop.Data.Struct.ItemDBEntry;
import net.cubespace.RegionShop.Events.ItemDB.PreEnableEvent;
import net.cubespace.RegionShop.Util.CSVReader;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.Bukkit;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:36
 *
 * This class is part of the internals for the ItemDB. Please do not instance an object from this.
 */
public class ItemDB extends CSVReader {
    /**
     * Load the CSV from db/dataValues.csv in the JAR
     */
    public ItemDB() {
        super(Plugin.getInstance().getResource("db/dataValues.csv"));
    }

    /**
     * The line from the CSV Parser
     *
     * 0 => ItemID      (Integer)
     * 1 => DataValue   (Short)
     * 2 => DataName    (String)
     * 3 => ItemName    (String)
     * 4 => MC Version  (String)
     */
    @Override
    public void onLine(String[] line) {
        ItemDBEntry entry = new ItemDBEntry();

        try {
            Logger.debug("ItemDB - Got new Line: " + line[0] + " - " + line[1] + " - " + line[2] + " - " + line[3] + " - " + line[4]);

            entry.setItemID(Integer.parseInt(line[0]));
            entry.setDataValue(Short.parseShort(line[1]));
            entry.setDataName(line[2]);
            entry.setItemName(line[3]);
            entry.setMcVersion(line[4]);
        } catch (NumberFormatException e) {
            Logger.fatal("CSV Line in wrong format", e);
            return;
        }

        ItemDBStorage.add(entry);
    }

    /**
     * The end of the File has reached.
     */
    @Override
    public void onEnd() {
        //Fire the Event
        Bukkit.getPluginManager().callEvent(new PreEnableEvent());

        ItemDBStorage.enable();
    }
}
