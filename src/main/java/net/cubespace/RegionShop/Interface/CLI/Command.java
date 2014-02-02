package net.cubespace.RegionShop.Interface.CLI;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String command();
    int arguments();
    String permission();
    String helpKey();
    String helpPage();
}
