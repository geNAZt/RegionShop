package com.geNAZt.RegionShop.Database.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.08.13
 */
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by id")
@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "ShopShowcaseItem")
public class ShopShowcaseItem {
    @Id
    private Integer id;
    private String owner;
    private String world;
    private String shop;
    private Integer item;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }
}