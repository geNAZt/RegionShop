package com.geNAZt.RegionShop.Data.Storage;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 13.10.13 11:14
 */
public class Update {
    private static boolean update;
    private static String link;
    private static String version;

    public static boolean isUpdate() {
        return update;
    }

    public static void setUpdate(boolean update) {
        Update.update = update;
    }

    public static String getLink() {
        return link;
    }

    public static void setLink(String link) {
        Update.link = link;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        Update.version = version;
    }
}
