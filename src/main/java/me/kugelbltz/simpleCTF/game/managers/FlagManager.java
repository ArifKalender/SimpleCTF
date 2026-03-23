package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.UtilizationMethods.removeFlag;

public class FlagManager {

    private final Map<Team, Location> flagLocations = new HashMap<>();
    private final Map<Team, Entity> flagCarriers = new HashMap<>();
    private final Match match;

    public FlagManager(Match match) {
        this.match = match;
    }

    /**
     * Plays animations of the flags and flag carriers
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void playFlagAnimation(Team team, Material banner, Material particleColorSource) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        // --- Block particles for flags ---
        Location flagLoc = this.getFlagLocation(team);
        boolean available = flagLoc.getBlock().getType() == banner;
        if (available) flagLoc.getWorld().spawnParticle(Particle.FALLING_DUST, flagLoc,
                10, 1.5, 1.5, 1.5, particleColorSource.createBlockData());
        else flagLoc.getWorld().spawnParticle(Particle.CRIT, flagLoc,
                10, 0.5, 0.5, 0.5, 0.05);

        // --- Player particles for flag carriers ---
        Entity carrier = this.getFlagCarrier(team);
        if (carrier != null) {
            carrier.getWorld().spawnParticle(Particle.FALLING_DUST, carrier.getLocation(),
                    10, 1, 1, 1, particleColorSource.createBlockData());
        }
    }

    /**
     * If {@code place} is {@code true}, then it places the BANNER blocks at the flag locations. If else, places AIR blocks.
     *
     * @param place Whether to place the banners
     */
    public void loadFlags(boolean place) {
        if (place) {
            this.getFlagLocation(Team.RED).getBlock().setType(Material.RED_BANNER);
            this.getFlagLocation(Team.BLUE).getBlock().setType(Material.BLUE_BANNER);
        } else {
            this.getFlagLocation(Team.RED).getBlock().setType(Material.AIR);
            this.getFlagLocation(Team.BLUE).getBlock().setType(Material.AIR);
        }
    }


    /**
     * Handles the given flag and nearby entities to it. Saving your own flag or returning the enemy's flag to your base is handled here.
     */
    public void handleFlag(Team flag, Material bannerType) {
        for (LivingEntity lEntity : this.getFlagLocation(flag).getNearbyLivingEntities(3)) {
            if (!(lEntity instanceof Player player)) continue;
            Team loopPlayerTeam = Team.getTeam(player);
            Team enemyTeam = Team.getOpposite(flag);
            Entity flagCarrierFunc = this.getFlagCarrier(flag);
            Entity flagCarrierEnemy = this.getFlagCarrier(enemyTeam);
            Location flagLoc = this.getFlagLocation(flag);

            if (loopPlayerTeam == flag) {
                if (flagCarrierFunc != null && flagCarrierFunc.equals(player)) {
                    saveOwnFlag(player, flagLoc, bannerType, flag);
                }
                if (flagCarrierEnemy != null && flagCarrierEnemy.equals(player)) {
                    returnFlag(player, loopPlayerTeam, enemyTeam);
                }
            }
        }
    }

    /**
     * Saves the flag for the given parameters, placing the block and removing the item from inventory, then broadcasting the message.
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    private void saveOwnFlag(Player player, Location flagLoc, Material bannerType, Team team) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        flagLoc.getBlock().setType(bannerType);
        removeFlag(player, team);
        match.getMessageManager().broadcastMessage(getMM().deserialize(StaticVariables.PLAYER_PLACE_FLAG.replace("%player%", player.getName())));
    }

    /**
     * Makes it so the given player has returned the capturedTeam's flag to scoringTeam
     *
     * @param player       The player who returned the flag
     * @param scoringTeam  The team that returned the flag
     * @param capturedTeam The team who had their flag stolen
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    private void returnFlag(Player player, Team scoringTeam, Team capturedTeam) {
        if (scoringTeam == Team.NONE || capturedTeam == Team.NONE)
            throw new IllegalArgumentException("Team NONE is not allowed");
        match.getScoreManager().setScore(scoringTeam, match.getScoreManager().getScore(scoringTeam) + 1);
        match.initPlayers(Team.RED, false);
        match.initPlayers(Team.BLUE, false);
        match.getFlagManager().loadFlags(true);
        removeFlag(player, capturedTeam);
        match.getMessageManager().broadcastMessage(getMM().deserialize(
                StaticVariables.PLAYER_RETURN_FLAG
                        .replace("%player%", player.getName())
                        .replace("%opposite_color%", capturedTeam.name())
        ));
        Bukkit.getPluginManager().callEvent(new FlagScoreEvent(player, scoringTeam, capturedTeam));
    }


    /**
     * Broadcasts the dropped flag's location.
     *
     * @param team     The team of the flag
     * @param dropper  The one who dropped the flag
     * @param location The location at which the flag was dropped
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void broadcastFlagDropLocation(Team team, Player dropper, Location location) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        String locString = "X: " + (int) location.getX() + " | Y: " + (int) location.getY() + " | Z: " + (int) location.getZ();
        Component component = getMM().deserialize(StaticVariables.FLAG_DROPPED_AT
                .replace("%player%", dropper.getName())
                .replace("%color%", team.name().toUpperCase(Locale.ENGLISH))
                .replace("%coordinates%", locString));
        match.getMessageManager().broadcastMessage(component);
    }


    /**
     * @return A copy of the flag location for the given team
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public Location getFlagLocation(Team team) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        return flagLocations.get(team).clone();
    }

    /**
     * Sets the flag location for the given team
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void setFlagLocation(Team team, Location newLocation) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        flagLocations.put(team, newLocation);
    }

    /**
     * @return the flag carrier for the given team
     */
    public Entity getFlagCarrier(Team flagColor) {
        return this.flagCarriers.get(flagColor);
    }

    /**
     * Sets the flag carrier for the given team
     */
    public void setFlagCarrier(@Nullable Entity entity, @NotNull Team flagColor) {
        this.flagCarriers.put(flagColor, entity);
    }
}
