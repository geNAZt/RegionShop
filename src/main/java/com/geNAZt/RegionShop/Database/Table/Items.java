package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */

import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;
import java.util.Set;

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by id")
@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "rs_items")
public class Items {
    @Id
    private Integer id;

    @ManyToOne
    protected ItemStorage itemStorage;

    @ManyToOne
    protected ItemMeta meta;

    @OneToMany
    protected Set<Enchantment> enchantments;

    private Float buy;
    private Float sell;
    private Integer currentAmount;
    private Integer unitAmount;

    private String customName;
    private String owner;

    private Short durability;

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

    public Integer getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Integer currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Float getBuy() {
        return buy;
    }

    public void setBuy(Float buy) {
        this.buy = buy;
    }

    public Float getSell() {
        return sell;
    }

    public void setSell(Float sell) {
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

    public ItemMeta getMeta() {
        return meta;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    public void setItemStorage(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<Enchantment> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Set<Enchantment> enchantments) {
        this.enchantments = enchantments;
    }
}