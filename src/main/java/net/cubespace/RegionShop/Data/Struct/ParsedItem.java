package net.cubespace.RegionShop.Data.Struct;

public class ParsedItem {
    public final Integer itemID;
    public final Byte dataValue;

    public ParsedItem(Integer itemID, Byte dataValue) {
        this.itemID = itemID;
        this.dataValue = dataValue;
    }
}
