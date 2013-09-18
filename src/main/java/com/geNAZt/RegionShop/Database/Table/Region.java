package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */

import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;
import java.util.List;

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by id")
@Entity()
@Table(name = "rs_region")
public class Region {
    @Id
    private Integer id;
    @ManyToOne
    private ItemStorage itemStorage;
    private String name;
    private String region;
    private String world;
    private Double minX;
    private Double minY;
    private Double minZ;
    private Double maxX;
    private Double maxY;
    private Double maxZ;

    @ManyToMany(mappedBy="ownsRegions", cascade=CascadeType.ALL)
    private List<Player> owners;

    @ManyToMany(mappedBy="memberInRegions", cascade=CascadeType.ALL)
    private List<Player> members;

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

    public List<Player> getOwners() {
        return owners;
    }

    public void setOwners(List<Player> owners) {
        this.owners = owners;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public Double getMaxY() {
        return maxY;
    }

    public void setMaxY(Double maxY) {
        this.maxY = maxY;
    }

    public Double getMinX() {
        return minX;
    }

    public void setMinX(Double minX) {
        this.minX = minX;
    }

    public Double getMinY() {
        return minY;
    }

    public void setMinY(Double minY) {
        this.minY = minY;
    }

    public Double getMinZ() {
        return minZ;
    }

    public void setMinZ(Double minZ) {
        this.minZ = minZ;
    }

    public Double getMaxX() {
        return maxX;
    }

    public void setMaxX(Double maxX) {
        this.maxX = maxX;
    }

    public Double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(Double maxZ) {
        this.maxZ = maxZ;
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    public void setItemStorage(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }


}