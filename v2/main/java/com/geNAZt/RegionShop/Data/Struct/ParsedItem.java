package com.geNAZt.RegionShop.Data.Struct;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 20.07.13
 */
public class ParsedItem {
    public final Integer itemID;
    public final Byte dataValue;

    public ParsedItem(Integer itemID, Byte dataValue) {
        this.itemID = itemID;
        this.dataValue = dataValue;
    }
}
