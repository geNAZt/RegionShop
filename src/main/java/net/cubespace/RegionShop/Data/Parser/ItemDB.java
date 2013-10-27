package net.cubespace.RegionShop.Data.Parser;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Data.Storage.ItemDBStorage;
import net.cubespace.RegionShop.Data.Struct.ItemDBEntry;
import net.cubespace.RegionShop.Util.CSVReader;
import net.cubespace.RegionShop.Util.Logger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:36
 */
public class ItemDB extends CSVReader {
    public ItemDB() {
        super(Plugin.getInstance().getResource("db/dataValues.csv"));
    }

    @Override
    public void onLine(String[] line) {
        Logger.debug("ItemDB - Got new Line: " + line[0] + " - " + line[1] + " - " + line[2] + " - " + line[3] + " - " + line[4]);

        /*
         * 0 => ItemID
         * 1 => DataValue
         * 2 => DataName
         * 3 => ItemName
         * 4 => MC Version
         */
        ItemDBEntry entry = new ItemDBEntry();

        try {
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
}
