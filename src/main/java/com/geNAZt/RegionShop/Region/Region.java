package com.geNAZt.RegionShop.Region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 28.06.13
 */
public class Region {
    private ProtectedRegion region;
    private String name;
    private String itemStorage;
    private boolean bundle;

    public ProtectedRegion getRegion() {
        return region;
    }

    public void setRegion(ProtectedRegion region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemStorage() {
        return itemStorage;
    }

    public void setItemStorage(String itemStorage) {
        this.itemStorage = itemStorage;
    }

    public boolean isBundle() {
        return bundle;
    }

    public void setBundle(boolean bundle) {
        this.bundle = bundle;
    }
}
