package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.field.DatabaseField;

import javax.persistence.Entity;

@Entity()
public class ItemMeta {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(uniqueCombo = true)
    private Integer itemID;
    @DatabaseField(uniqueCombo = true)
    private Short dataValue;

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

    public Short getDataValue() {
        return dataValue;
    }

    public void setDataValue(Short dataValue) {
        this.dataValue = dataValue;
    }
}
