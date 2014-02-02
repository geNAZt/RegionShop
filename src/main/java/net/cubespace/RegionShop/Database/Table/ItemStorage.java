package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity()
public class ItemStorage {
    @DatabaseField(generatedId = true)
    private Integer id;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Region> regions;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Chest> chests;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Items> items;
    @Column
    private String setting;
    @Column
    private String name;
    @Column
    private Integer itemAmount = 0;
    @Column
    private boolean servershop = false;

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ForeignCollection<Region> getRegions() {
        return regions;
    }

    public void setRegions(ForeignCollection<Region> regions) {
        this.regions = regions;
    }

    public ForeignCollection<Items> getItems() {
        return items;
    }

    public void setItems(ForeignCollection<Items> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(Integer itemAmount) {
        this.itemAmount = itemAmount;
    }

    public boolean isServershop() {
        return servershop;
    }

    public void setServershop(boolean servershop) {
        this.servershop = servershop;
    }

    public ForeignCollection<Chest> getChests() {
        return chests;
    }

    public void setChests(ForeignCollection<Chest> chests) {
        this.chests = chests;
    }
}
