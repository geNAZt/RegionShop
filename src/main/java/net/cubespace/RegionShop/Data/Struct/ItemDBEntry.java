package net.cubespace.RegionShop.Data.Struct;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 18:48
 *
 * This class represents one Entry in the ItemDB
 */
public class ItemDBEntry {
    private Integer itemID;
    private Short dataValue;
    private String dataName;
    private String itemName;
    private String mcVersion;

    public String getMcVersion() {
        return mcVersion;
    }

    public void setMcVersion(String mcVersion) {
        this.mcVersion = mcVersion;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Short getDataValue() {
        return dataValue;
    }

    public void setDataValue(Short dataValue) {
        this.dataValue = dataValue;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
