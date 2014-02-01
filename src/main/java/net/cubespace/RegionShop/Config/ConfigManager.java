package net.cubespace.RegionShop.Config;

import net.cubespace.RegionShop.Config.Files.Groups;
import net.cubespace.RegionShop.Config.Files.Language;
import net.cubespace.RegionShop.Config.Files.Main;
import net.cubespace.RegionShop.Config.Files.Version;

/**
 *
 */
public class ConfigManager {
    public static Main main;
    public static Groups groups;
    public static Version version;
    public static Language language;

    static {
        main = new Main();
        main.init();

        groups = new Groups();
        groups.init();

        version = new Version();
        version.init();

        language = new Language();
        language.init();
    }
}
