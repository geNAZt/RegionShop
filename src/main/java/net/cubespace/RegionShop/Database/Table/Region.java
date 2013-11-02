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
    @DatabaseField(foreign = true, columnName = "itemstorage_id")
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
    @ForeignCollectionField(eager = false)
    private ForeignCollection<CustomerSign> customerSigns;

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
}