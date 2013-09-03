package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class ConfigManager {
    public static DBConfig db;
    public static TimingConfig timings;
    public static Language language;
    public static Misc misc;

    public static void init(RegionShopPlugin plugin) {
        try {
            db = new DBConfig(plugin);
            db.init();

            timings = new TimingConfig(plugin);
            timings.init();

            language = new Language(plugin);
            language.init();

            misc = new Misc(plugin);
            misc.init();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}