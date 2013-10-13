package com.geNAZt.RegionShop.Database.Table;

import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.annotation.CacheTuning;
import com.geNAZt.RegionShop.Database.ItemStorageHolder;

import javax.persistence.*;
import java.util.List;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.09.13
 */

@CacheTuning(maxIdleSecs=30, maxSecsToLive=3600, maxSize=5000)
@CacheStrategy(useBeanCache=true, readOnly=true, warmingQuery="order by id")
@Entity()
@Table(name = "rs_chest")
public class Chest implements ItemStorageHolder {
    @Id
    @Version
    private Integer id;

    @ManyToOne
    private ItemStorage itemStorage;
    private String name;
    private String world;
    private Integer chestX;
    private Integer chestY;
    private Integer chestZ;
    private Integer signX;
    private Integer signY;
    private Integer signZ;

    @ManyToMany(mappedBy="ownsChests", cascade=CascadeType.ALL)
    private List<Player> owners;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    public void setItemStorage(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Integer getChestX() {
        return chestX;
    }

    public void setChestX(Integer chestX) {
        this.chestX = chestX;
    }

    public Integer getChestY() {
        return chestY;
    }

    public void setChestY(Integer chestY) {
        this.chestY = chestY;
    }

    public Integer getChestZ() {
        return chestZ;
    }

    public void setChestZ(Integer chestZ) {
        this.chestZ = chestZ;
    }

    public Integer getSignX() {
        return signX;
    }

    public void setSignX(Integer signX) {
        this.signX = signX;
    }

    public Integer getSignY() {
        return signY;
    }

    public void setSignY(Integer signY) {
        this.signY = signY;
    }

    public Integer getSignZ() {
        return signZ;
    }

    public void setSignZ(Integer signZ) {
        this.signZ = signZ;
    }

    public List<Player> getOwners() {
        return owners;
    }

    public void setOwners(List<Player> owners) {
        this.owners = owners;
    }
}
