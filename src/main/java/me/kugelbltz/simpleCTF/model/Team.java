package me.kugelbltz.simpleCTF.model;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.getBannerItems;

public enum Team {

    RED(Material.RED_BANNER, Material.RED_CONCRETE, Color.RED),
    BLUE(Material.BLUE_BANNER, Material.BLUE_CONCRETE, Color.BLUE),
    NONE(Material.GRAY_BANNER, Material.GRAY_CONCRETE, Color.GRAY);

    private final Material bannerItem, particleSource;
    private final Color rgbColor;
    Team(Material bannerItem, Material particleSource, Color rgbColor) {
        this.bannerItem = bannerItem;
        this.particleSource = particleSource;
        this.rgbColor = rgbColor;
    }


    /**
     * @return The enemy team of the given team
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public static Team getOpposite(Team team) {
        Team.requirePlayableTeam(team);
        if (team == RED) return BLUE;
        else if (team == BLUE) return RED;
        else return null; // Unreachable in practice, as requirePlayableTeam ensures this.
    }

    /**
     * @return The {@code ItemStack} item of the given team, refer to {@code BannerItems}
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public static ItemStack getTeamFlag(Team team) {
        requirePlayableTeam(team);
        return getBannerItems().getFlag(team);
    }

    public Material getBannerItem() {
        return bannerItem;
    }

    public Material getParticleSource() {
        return particleSource;
    }

    public static List<Team> playableTeams() {
        return Arrays.asList(Team.RED, Team.BLUE);
    }

    public static void requirePlayableTeam(Team team) {
        if (!playableTeams().contains(team)) throw new IllegalArgumentException("Illegal team: " + team.name().toUpperCase(Locale.ENGLISH));
    }

    public Color getTeamRGB() {
        return rgbColor;
    }
}
