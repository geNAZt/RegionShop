package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import javax.persistence.Entity;

@Entity()
public class Player {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(unique = true)
    private String name;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<PlayerOwnsChest> ownsChest;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<PlayerOwnsRegion> ownsRegion;

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

    public ForeignCollection<PlayerOwnsChest> getOwnsChest() {
        return ownsChest;
    }

    public void setOwnsChest(ForeignCollection<PlayerOwnsChest> ownsChest) {
        this.ownsChest = ownsChest;
    }

    public ForeignCollection<PlayerOwnsRegion> getOwnsRegion() {
        return ownsRegion;
    }

    public void setOwnsRegion(ForeignCollection<PlayerOwnsRegion> ownsRegion) {
        this.ownsRegion = ownsRegion;
    }
}
