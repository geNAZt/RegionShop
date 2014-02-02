package net.cubespace.RegionShop.Database.Table;

import com.j256.ormlite.field.DatabaseField;
import net.cubespace.RegionShop.Database.PlayerOwns;

import javax.persistence.Entity;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
@Entity()
public class PlayerOwnsChest implements PlayerOwns {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, columnName = "player_id", foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
    private Player player;
    @DatabaseField(foreign = true, columnName = "chest_id", foreignAutoRefresh=true, maxForeignAutoRefreshLevel=3)
    private Chest chest;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Chest getChest() {
        return chest;
    }

    public void setChest(Chest chest) {
        this.chest = chest;
    }
}
