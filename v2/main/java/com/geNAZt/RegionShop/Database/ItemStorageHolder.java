package com.geNAZt.RegionShop.Database;

import com.geNAZt.RegionShop.Database.Table.ItemStorage;
import com.geNAZt.RegionShop.Database.Table.Player;

import java.util.List;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.09.13
 */
public interface ItemStorageHolder {
    public ItemStorage getItemStorage();
    public void setItemStorage(ItemStorage itemStorage);
    public String getName();
    public void setName(String name);
    public List<Player> getOwners();
    public void setOwners(List<Player> owners);
    public String getWorld();
    public void setWorld(String world);
}
