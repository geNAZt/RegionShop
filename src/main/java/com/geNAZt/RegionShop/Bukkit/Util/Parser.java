package com.geNAZt.RegionShop.Bukkit.Util;

import com.geNAZt.RegionShop.Data.Struct.ParsedItem;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 20.07.13
 */
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
