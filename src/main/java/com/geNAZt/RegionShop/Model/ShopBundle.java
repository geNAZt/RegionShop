package com.geNAZt.RegionShop.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by region")
@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "ShopBundle")
public class ShopBundle {
    @Id
    private Integer id;
    private String name;
    private String region;
    private String world;
    private Boolean master;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Boolean getMaster() {
        return master;
    }

    public Boolean isMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }
}