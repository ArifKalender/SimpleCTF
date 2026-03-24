package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.getBannerItems;

public enum Team {

    RED(Material.RED_BANNER, Material.RED_CONCRETE),
    BLUE(Material.BLUE_BANNER, Material.BLUE_CONCRETE),
    NONE(Material.GRAY_BANNER, Material.GRAY_CONCRETE);

    private final Material bannerItem, particleSource;
    Team(Material bannerItem, Material particleSource) {
        this.bannerItem = bannerItem;
        this.particleSource = particleSource;
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

    public static Color getTeamRGB(Team team) {
        if (team == Team.RED) return Color.fromRGB(255, 0, 0);
        else if (team == Team.BLUE) return Color.fromRGB(0, 0, 255);
        else return Color.GRAY;
    }
}
