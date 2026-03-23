package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
     * @return The team of the given player
     */
    public static Team getTeam(Player player) {
        Match match = SimpleCTF.getCurrentMatch();
        if (match == null) return NONE;
        if (match.getPlayers(RED).contains(player)) return RED;
        if (match.getPlayers(BLUE).contains(player)) return BLUE;
        return NONE;
    }

    /**
     * @return The enemy team of the given team
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public static Team getOpposite(Team team) {
        if (team == RED) return BLUE;
        else if (team == BLUE) return RED;
        else throw new IllegalArgumentException("Team NONE is not allowed");
    }

    /**
     * @return The {@code ItemStack} item of the given team, refer to {@code BannerItems}
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public static ItemStack getTeamFlag(Team team) {
        if (team == RED) return getBannerItems().getRedFlag();
        else if (team == BLUE) return getBannerItems().getBlueFlag();
        else throw new IllegalArgumentException("Team NONE is not allowed");
    }

    /**
     * @return The Team of the given {@code ItemStack}, if given item is invalid returns {@code Team.NONE}
     */
    public static Team getTeamFromFlag(ItemStack itemStack) {
        if (getBannerItems().isBlueFlag(itemStack)) return BLUE;
        else if (getBannerItems().isRedFlag(itemStack)) return RED;
        else return NONE;
    }

    public Material getBannerItem() {
        return bannerItem;
    }

    public Material getParticleSource() {
        return particleSource;
    }
}
