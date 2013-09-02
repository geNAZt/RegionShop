package com.geNAZt.RegionShop.Database.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;
import java.util.List;

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by region")
@Entity()
@Table(name = "ShopRegion")
public class ShopRegion {
    @Id
    private Integer id;
    private String name;
    private String region;
    private String world;
    private Boolean bundle;
    private String owner;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="shopRegion")
    private List<ShopItems> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Boolean getBundle() {
        return bundle;
    }

    public Boolean isBundle() {
        return bundle;
    }

    public void setBundle(Boolean bundle) {
        this.bundle = bundle;
    }

    public List<ShopItems> getItems() {
        return items;
    }

    public void setItems(List<ShopItems> items) {
        this.items = items;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}