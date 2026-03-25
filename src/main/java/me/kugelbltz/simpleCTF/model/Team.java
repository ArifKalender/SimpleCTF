package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public enum Team {

    RED(Material.RED_BANNER, Material.RED_CONCRETE, Color.RED),
    BLUE(Material.BLUE_BANNER, Material.BLUE_CONCRETE, Color.BLUE);

    private final Material bannerItem, particleSource;
    private final Color rgbColor;

    Team(Material bannerItem, Material particleSource, Color rgbColor) {
        this.bannerItem = bannerItem;
        this.particleSource = particleSource;
        this.rgbColor = rgbColor;
    }


    /**
     * @return The enemy team of the given team
     */
    public static Team getOpposite(Team team) {
        if (team == RED) return BLUE;
        else if (team == BLUE) return RED;
        else return null;
    }

    /**
     * @return The {@code ItemStack} item of the given team, refer to {@code BannerItems}
     */
    public static ItemStack getTeamFlag(Team team) {
        if (team == null) return null;
        return SimpleCTF.getInstance().getBannerItems().getFlag(team);
    }

    public static List<Team> playableTeams() {
        return Arrays.asList(Team.RED, Team.BLUE);
    }

    public Material getBannerItem() {
        return bannerItem;
    }

    public Material getParticleSource() {
        return particleSource;
    }

    public Color getTeamRGB() {
        return rgbColor;
    }
}
