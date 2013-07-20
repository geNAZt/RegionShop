package com.geNAZt.RegionShop.Data.Struct;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 03.07.13
 */
public class SignEquip {
    public final String owner;
    public final String region;
    public final String world;

    public SignEquip(String owner, String region, String world) {
        this.owner = owner;
        this.region = region;
        this.world = world;
    }
}
