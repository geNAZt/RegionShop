package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 21.06.13
 */

import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.annotation.CacheTuning;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@CacheTuning(maxIdleSecs=30, maxSecsToLive=3600, maxSize=5000)
@CacheStrategy(useBeanCache=true, readOnly=true, warmingQuery="order by id")
@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "rs_itemaverage")
public class ServerItemAverage {
    @Id
    private Integer id;
    private Integer sold;
    private Integer bought;
    private Date date;
    private Boolean servershop;
    @ManyToOne
    private ItemMeta meta;
    @ManyToOne
    private Region region;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public Integer getBought() {
        return bought;
    }

    public void setBought(Integer bought) {
        this.bought = bought;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Boolean getServershop() {
        return servershop;
    }

    public void setServershop(Boolean servershop) {
        this.servershop = servershop;
    }
}
