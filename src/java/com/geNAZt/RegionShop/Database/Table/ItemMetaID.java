package com.geNAZt.RegionShop.Database.Table;

import javax.persistence.Embeddable;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 01.09.13
 */

@Embeddable
public class ItemMetaID {
    private Integer itemID;
    private Byte dataValue;

    public ItemMetaID() {

    }

    public ItemMetaID(Integer itemID, Byte dataValue) {
        this.itemID = itemID;
        this.dataValue = dataValue;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Byte getDataValue() {
        return dataValue;
    }

    public void setDataValue(Byte dataValue) {
        this.dataValue = dataValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ItemMetaID other = (ItemMetaID) obj;
        return !((this.itemID == null) ? (other.itemID != null) : !this.itemID.equals(other.itemID)) && !((this.dataValue == null) ? (other.dataValue != null) : !this.dataValue.equals(other.dataValue));
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 89 * hash + (this.itemID != null ? this.itemID.hashCode() : 0);
        hash = 89 * hash + (this.dataValue != null ? this.dataValue.hashCode() : 0);

        return hash;
    }
}
