package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 01.09.13
 */

import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;

@SuppressWarnings("UnusedDeclaration")
@CacheStrategy(useBeanCache=true, readOnly=false)
@Entity()
@Table(name = "ItemMeta")
public class ItemMeta {
    @EmbeddedId
    private ItemMetaID id;

    private String name;
    private Integer maxStackSize;
    private short maxDurability;

    public ItemMetaID getId() {
        return id;
    }

    public void setId(ItemMetaID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public short getMaxDurability() {
        return maxDurability;
    }

    public void setMaxDurability(short maxDurability) {
        this.maxDurability = maxDurability;
    }
}
