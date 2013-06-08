package com.geNAZt.RegionShop.Model;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 05.06.13
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */

import javax.persistence.*;

@Entity()
@Table(name = "ShopItems")
public class ShopItems {
    @Id
    private Integer id;
    private String owner;
    private String region;
    private String world;
    private Integer currentAmount;
    private Integer buy;
    private Integer sell;
    private Integer unitAmount;
    private Integer itemID;
    private String customName;
    private Byte dataID;
    private Short durability;
    private boolean stackable;

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public Short getDurability() {
        return durability;
    }

    public void setDurability(Short durability) {
        this.durability = durability;
    }

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

    public Byte getDataID() {
        return dataID;
    }

    public void setDataID(Byte dataID) {
        this.dataID = dataID;
    }

    public Integer getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Integer currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Integer getBuy() {
        return buy;
    }

    public void setBuy(Integer buy) {
        this.buy = buy;
    }

    public Integer getSell() {
        return sell;
    }

    public void setSell(Integer sell) {
        this.sell = sell;
    }

    public Integer getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(Integer unitAmount) {
        this.unitAmount = unitAmount;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}