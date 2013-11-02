package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.field.DatabaseField;
import net.cubespace.RegionShop.Database.ItemStorageHolder;

import javax.persistence.*;

@Entity()
public class Chest implements ItemStorageHolder {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, columnName = "itemstorage_id")
    private ItemStorage itemStorage;
    @Column
    private String name;
    @Column
    private String world;
    @Column
    private Integer chestX;
    @Column
    private Integer chestY;
    @Column
    private Integer chestZ;
    @Column
    private Integer signX;
    @Column
    private Integer signY;
    @Column
    private Integer signZ;
    @DatabaseField(foreign = true, columnName = "owner_id")
    private Player owner;

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

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
