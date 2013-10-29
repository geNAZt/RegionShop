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

    /**
     * Represents the first Minecraft Version the item appeard (0.0.0 was before the official releases)
     *
     * @return MC Version in "major.minor.bugfix" Format
     */
    public String getMcVersion() {
        return mcVersion;
    }

    /**
     * Sets the first appearing Version
     *
     * @warning This should only be used by the ItemDB Parser
     * @param mcVersion The Minecraft Version in "major.minor.bugfix" Format
     */
    public void setMcVersion(String mcVersion) {
        this.mcVersion = mcVersion;
    }

    /**
     * Represents the ItemID which has been given in Minecraft
     *
     * @return The ItemID in Minecraft
     */
    public Integer getItemID() {
        return itemID;
    }

    /**
     * Sets the ItemID from Minecraft
     *
     * @warning This should only be used by the ItemDB Parser
     * @param itemID The ItemID from Minecraft
     */
    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    /**
     * Represents the DataValue from Minecraft (attention this is NOT byte, since Minecraft uses short for DataValues)
     *
     * @return The Datavalue from Minecraft
     */
    public Short getDataValue() {
        return dataValue;
    }

    /**
     * Sets the DataValue from Minecraft
     *
     * @warning This should only be used by the ItemDB Parser
     * @param dataValue The Datavalue from Minecraft
     */
    public void setDataValue(Short dataValue) {
        this.dataValue = dataValue;
    }

    /**
     * The correct prefix for this DataValue
     *
     * @return Prefix for this Datavalue
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * Sets the Dataname for this Datavalue
     *
     * @warning This should only be used by the ItemDB Parser
     * @param dataName The correct Dataname for this Datavalue
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * The correct Itemname for this ItemID
     *
     * @return Itemname for this ItemID
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the correct ItemName for this ItemID
     *
     * @warning This should only be used by the ItemDB Parser
     * @param itemName The Itemname for this ID
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
