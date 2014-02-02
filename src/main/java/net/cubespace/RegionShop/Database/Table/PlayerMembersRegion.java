package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.field.DatabaseField;

import javax.persistence.Entity;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
@Entity()
public class PlayerMembersRegion {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, columnName = "player_id", foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
    private Player player;
    @DatabaseField(foreign = true, columnName = "region_id", foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
    private Region region;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
