package net.cubespace.RegionShop.Database;

import net.cubespace.RegionShop.Database.Table.ItemStorage;

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
    public String getWorld();
    public void setWorld(String world);
}
