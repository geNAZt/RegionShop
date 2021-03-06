package com.geNAZt.RegionShop.Config;

import com.geNAZt.RegionShop.Config.Files.Expert;
import com.geNAZt.RegionShop.Config.Files.Language;
import com.geNAZt.RegionShop.Config.Files.Main;
import com.geNAZt.RegionShop.Config.Files.Servershop;
import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class ConfigManager {
    public static Main main;
    public static Expert expert;
    public static Language language;
    public static Servershop servershop;

    public static void init(RegionShopPlugin plugin) {
        try {
            expert = new Expert(plugin);
            expert.init();

            language = new Language(plugin);
            language.init();

            main = new Main(plugin);
            main.init();

            servershop = new Servershop(plugin);
            servershop.init();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
