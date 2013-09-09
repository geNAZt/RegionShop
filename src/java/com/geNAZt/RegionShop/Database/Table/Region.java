package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.annotation.EnumMapping;
import com.geNAZt.RegionShop.Config.Group;
import com.geNAZt.RegionShop.Config.GroupRentMode;

import javax.persistence.*;
import java.util.List;

@Entity()
@Table(name = "Region")
public class Region {
    @Id
    private Integer id;
    private String name;
    private String region;
    private String world;
    private String currentGroup;
    private Boolean bundle;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="region")
    private List<Items> items;

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

    public Boolean getBundle() {
        return bundle;
    }

    public Boolean isBundle() {
        return bundle;
    }

    public void setBundle(Boolean bundle) {
        this.bundle = bundle;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
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

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }
}