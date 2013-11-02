package net.cubespace.RegionShop.Database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.schlichtherle.io.File;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Table.*;
import net.cubespace.RegionShop.Util.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 31.08.13
 */
public class Database {
    //Store the Connection
    private static ConnectionSource connectionSource;

    //Store the DAOs
    private static HashMap<Class<?>, Dao> daos = new HashMap<Class<?>, Dao>();

    static {
        try {
            //Try to connect to the Database
            connectionSource = new JdbcPooledConnectionSource(ConfigManager.main.DB_url.replace("{DIR}", Plugin.getInstance().getDataFolder().getAbsolutePath() + File.separator), ConfigManager.main.DB_username, ConfigManager.main.DB_password);

            Logger.debug("Connected to Database: " + connectionSource.getDatabaseType().getDatabaseName());

            //Setup the DAOs
            daos.put(Player.class, DaoManager.createDao(connectionSource, Player.class));
            daos.put(Region.class, DaoManager.createDao(connectionSource, Region.class));
            daos.put(Items.class, DaoManager.createDao(connectionSource, Items.class));
            daos.put(ItemMeta.class, DaoManager.createDao(connectionSource, ItemMeta.class));
            daos.put(ItemStorage.class, DaoManager.createDao(connectionSource, ItemStorage.class));
            daos.put(Enchantment.class, DaoManager.createDao(connectionSource, Enchantment.class));
            daos.put(Transaction.class, DaoManager.createDao(connectionSource, Transaction.class));
            daos.put(CustomerSign.class, DaoManager.createDao(connectionSource, CustomerSign.class));
            daos.put(Chest.class, DaoManager.createDao(connectionSource, Chest.class));

            //Create the tables if not existing
            for(Map.Entry<Class<?>, Dao> dao : daos.entrySet()) {
                TableUtils.createTableIfNotExists(connectionSource, dao.getKey());
            }
        } catch (SQLException e) {
            Logger.fatal("Could not connect to the Database", e);
        }
    }

    public static Dao getDAO(Class<?> entityClass) {
        return daos.get(entityClass);
    }
}
