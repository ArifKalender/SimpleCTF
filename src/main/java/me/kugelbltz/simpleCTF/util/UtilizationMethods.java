package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UtilizationMethods {
    /**
     * Deletes the force loaded chunks for the given world file name.
     *
     * @param worldFileName
     */
    public static void deleteForceLoadedChunks(String worldFileName) {
        File container = Bukkit.getWorldContainer();
        File worldFolder = new File(container, worldFileName);
        File dataFolder = new File(worldFolder, "data");
        File chunksFile = new File(dataFolder, "chunks.dat");
        if (chunksFile.exists() && chunksFile.length() > 100) {
            boolean deleted = chunksFile.delete();
            if (deleted) {
                SimpleCTF.getInstance().getLogger().info(
                        "Optimization: Detected forced chunks in " + worldFolder.getName() +
                                " (" + chunksFile.length() + " bytes). Deleted chunks.dat to fix load times."
                );
            }
        }
    }
}
