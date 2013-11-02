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
    private ForeignCollection<Chest> ownsChest;

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

    public ForeignCollection<Chest> getOwnsChest() {
        return ownsChest;
    }

    public void setOwnsChest(ForeignCollection<Chest> ownsChest) {
        this.ownsChest = ownsChest;
    }
}
