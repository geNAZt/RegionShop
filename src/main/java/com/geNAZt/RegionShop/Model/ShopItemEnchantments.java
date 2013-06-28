package com.geNAZt.RegionShop.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "ShopItemEnchantments")
public class ShopItemEnchantments {
    @Id
    private Integer id;
    private Integer shopItemId;
    private Integer enchId;
    private Integer enchLvl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopItemId() {
        return shopItemId;
    }

    public void setShopItemId(Integer shopItemId) {
        this.shopItemId = shopItemId;
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
}