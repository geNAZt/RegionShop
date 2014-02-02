package net.cubespace.RegionShop.Util;

import net.cubespace.RegionShop.Data.Struct.ParsedItem;

public class Parser {
    public static ParsedItem parseItemID(String itemString) {
        Integer itemID;
        byte dataValue = 0;

        if(itemString.contains(":")) {
            String[] itemIDAndData = itemString.split(":");

            try {
                dataValue = Byte.parseByte(itemIDAndData[1]);
                itemID = Integer.parseInt(itemIDAndData[0]);
            } catch(NumberFormatException ignored) {
                return null;
            }
        } else {
            try {
                itemID = Integer.parseInt(itemString);
            } catch(NumberFormatException ignored) {
                return null;
            }
        }

        return new ParsedItem(itemID, dataValue);
    }
}
