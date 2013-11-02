package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.field.DatabaseField;

import javax.persistence.*;

@Entity()
public class Enchantment {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, columnName = "item_id")
    private Items item;
    @Column
    private Integer enchId;
    @Column
    private Integer enchLvl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnchId() {
        return enchId;
    }

    public void setEnchId(Integer enchId) {
        this.enchId = enchId;
    }

    public Integer getEnchLvl() {
        return enchLvl;
    }

    public void setEnchLvl(Integer enchLvl) {
        this.enchLvl = enchLvl;
    }

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }
}