package com.geNAZt.RegionShop.Database.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */

import javax.persistence.*;

@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "Items")
public class ShopItems {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name="shop_region", referencedColumnName="id")
    protected ShopRegion shopRegion;

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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public ShopRegion getShopRegion() {
        return shopRegion;
    }

    public void setShopRegion(ShopRegion shopRegion) {
        this.shopRegion = shopRegion;
    }
}