package me.kugelbltz.simpleCTF.model;

import org.bukkit.*;

public class MatchMap {
    private static Location RED_LOCATION;
    private static Location BLUE_LOCATION;

    public static Location getRedLocation() {
        return RED_LOCATION;
    }

    public static void setRedLocation(Location redLocation) {
        RED_LOCATION = redLocation;
    }

    public static Location getBlueLocation() {
        return BLUE_LOCATION;
    }

    public static void setBlueLocation(Location blueLocation) {
        BLUE_LOCATION = blueLocation;
    }
}
