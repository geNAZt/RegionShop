package com.geNAZt.RegionShop.Database.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by id")
@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "ShopCustomerSign")
public class ShopCustomerSign {
    @Id
    private Integer id;
    private String owner;
    private String world;
    private String shop;
    private Integer x;
    private Integer y;
    private Integer z;
    private Integer itemid;
    private Byte datavalue;
    private Boolean isServershop;

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

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public Boolean getServershop() {
        return isServershop;
    }

    public void setServershop(Boolean servershop) {
        isServershop = servershop;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Byte getDatavalue() {
        return datavalue;
    }

    public void setDatavalue(Byte datavalue) {
        this.datavalue = datavalue;
    }
}