package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import net.cubespace.RegionShop.Database.ItemStorageHolder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Date;

@Entity()
public class Region implements ItemStorageHolder {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, columnName = "itemstorage_id", foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
    private ItemStorage itemStorage;
    @Column
    private String name;
    @Column
    private String lcName;
    @Column
    private String region;
    @Column
    private String world;
    @Column
    private Date lastRent;
    @Column
    private Double minX;
    @Column
    private Double minY;
    @Column
    private Double minZ;
    @Column
    private Double maxX;
    @Column
    private Double maxY;
    @Column
    private Double maxZ;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<CustomerSign> customerSigns;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<PlayerOwnsRegion> owners;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<PlayerMembersRegion> members;

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

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    public void setItemStorage(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public Date getLastRent() {
        return lastRent;
    }

    public void setLastRent(Date lastRent) {
        this.lastRent = lastRent;
    }

    public ForeignCollection<CustomerSign> getCustomerSigns() {
        return customerSigns;
    }

    public void setCustomerSigns(ForeignCollection<CustomerSign> customerSigns) {
        this.customerSigns = customerSigns;
    }

    public ForeignCollection<PlayerOwnsRegion> getOwners() {
        return owners;
    }

    public void setOwners(ForeignCollection<PlayerOwnsRegion> owners) {
        this.owners = owners;
    }

    public ForeignCollection<PlayerMembersRegion> getMembers() {
        return members;
    }

    public void setMembers(ForeignCollection<PlayerMembersRegion> members) {
        this.members = members;
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

    public Double getMaxY() {
        return maxY;
    }

    public void setMaxY(Double maxY) {
        this.maxY = maxY;
    }

    public Double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(Double maxZ) {
        this.maxZ = maxZ;
    }
}