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
@Table(name = "ShopItemEnchantmens")
public class ShopItemEnchantmens {
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