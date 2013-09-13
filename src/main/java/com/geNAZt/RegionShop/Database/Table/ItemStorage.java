package com.geNAZt.RegionShop.Database.Table;

import javax.persistence.*;
import java.util.Set;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 13.09.13
 */

@Entity()
@Table(name = "rs_itemstorage")
public class ItemStorage {
    @Id
    private Integer id;

    @OneToMany
    private Set<Region> regions;

    @OneToMany
    private Set<Items> items;
    private String setting;
    private String name;
    private Integer itemAmount = 0;

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

    public Set<Region> getRegions() {
        return regions;
    }

    public void setRegions(Set<Region> regions) {
        this.regions = regions;
    }

    public Set<Items> getItems() {
        return items;
    }

    public void setItems(Set<Items> items) {
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
}
