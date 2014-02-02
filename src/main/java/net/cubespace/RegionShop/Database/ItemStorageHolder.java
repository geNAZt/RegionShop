package net.cubespace.RegionShop.Database;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Database.Table.ItemStorage;

public interface ItemStorageHolder {
    public ItemStorage getItemStorage();
    public void setItemStorage(ItemStorage itemStorage);
    public String getName();
    public void setName(String name);
    public String getWorld();
    public void setWorld(String world);
    public ForeignCollection getOwners();
}
