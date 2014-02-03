package net.cubespace.RegionShop.Interface.Sign;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String command();
    String permission();
}
