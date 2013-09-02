package com.geNAZt.RegionShop.Database.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 01.09.13
 */

import javax.persistence.*;

@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "ItemMeta")
public class ItemMeta {
    @EmbeddedId
    private ItemMetaID id;

    private String name;
    private Integer itemID;
    private byte dataValue;
    private Integer maxStackSize;
    private short maxDurability;

    private Double sell;
    private Double buy;

    public ItemMetaID getId() {
        return id;
    }

    public void setId(ItemMetaID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public byte getDataValue() {
        return dataValue;
    }

    public void setDataValue(byte dataValue) {
        this.dataValue = dataValue;
    }

    public Double getSell() {
        return sell;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    public Double getBuy() {
        return buy;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public Integer getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public short getMaxDurability() {
        return maxDurability;
    }

    public void setMaxDurability(short maxDurability) {
        this.maxDurability = maxDurability;
    }
}
