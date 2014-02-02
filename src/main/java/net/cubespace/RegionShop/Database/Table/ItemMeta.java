package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.field.DatabaseField;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity()
public class ItemMeta {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(uniqueCombo = true)
    private Integer itemID;
    @DatabaseField(uniqueCombo = true)
    private Byte dataValue;
    @Column
    private Integer maxStackSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
}
