package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */

import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.annotation.CacheTuning;

import javax.persistence.*;

@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "rs_enchantment")
public class Enchantment {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name="items_id", referencedColumnName="id")
    private Items item;
    private Integer enchId;
    private Integer enchLvl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }
}