package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.GeneralUtils.removeFlag;

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
    public void playFlagAnimation(Team team) {
        Team.requirePlayableTeam(team);
        // --- Block particles for flags ---
        Location flagLoc = this.getFlagLocation(team);
        boolean available = flagLoc.getBlock().getType() == team.getBannerItem();
        if (available) flagLoc.getWorld().spawnParticle(Particle.FALLING_DUST, flagLoc,
                10, 1.5, 1.5, 1.5, team.getParticleSource().createBlockData());
        else flagLoc.getWorld().spawnParticle(Particle.CRIT, flagLoc,
                10, 0.5, 0.5, 0.5, 0.05);

        // --- Player particles for flag carriers ---
        Entity carrier = this.getFlagCarrier(team);
        if (carrier != null) {
            carrier.getWorld().spawnParticle(Particle.FALLING_DUST, carrier.getLocation(),
                    10, 1, 1, 1, team.getParticleSource().createBlockData());
        }
    }

    /**
     * If {@code place} is {@code true}, then it places the BANNER blocks at the flag locations. If else, places AIR blocks.
     *
     * @param place Whether to place the banners
     */
    public void loadFlags(boolean place) {
        if (place) {
            this.getFlagLocation(Team.RED).getBlock().setType(Team.RED.getBannerItem());
            this.getFlagLocation(Team.BLUE).getBlock().setType(Team.BLUE.getBannerItem());
        } else {
            this.getFlagLocation(Team.RED).getBlock().setType(Material.AIR);
            this.getFlagLocation(Team.BLUE).getBlock().setType(Material.AIR);
        }
    }


    /**
     * Handles the given flag and nearby entities to it. Saving your own flag or returning the enemy's flag to your base is handled here.
     */
    public void handleFlag(Team flag) {
        if (!Team.playableTeams().contains(flag)) return;
        for (LivingEntity lEntity : this.getFlagLocation(flag).getNearbyLivingEntities(3)) {
            if (!(lEntity instanceof Player player)) continue;
            Team loopPlayerTeam = Team.getTeam(player);
            Team enemyTeam = Team.getOpposite(flag);
            Entity carrierOfThisFlag = this.getFlagCarrier(flag);
            Entity carrierOfEnemyFlag = this.getFlagCarrier(enemyTeam);

            if (loopPlayerTeam == flag) {
                if (carrierOfThisFlag != null && carrierOfThisFlag.equals(player)) {
                    restoreFlag(player, flag);
                }
                if (carrierOfEnemyFlag != null && carrierOfEnemyFlag.equals(player)) {
                    captureFlagAndScore(player, loopPlayerTeam, enemyTeam);
                }
            }
        }
    }

    /**
     * Saves the flag for the given parameters, placing the block and removing the item from inventory, then broadcasting the message.
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void restoreFlag(Player player, Team team) {
        Team.requirePlayableTeam(team);
        setFlagCarrier(null, team);
        getFlagLocation(team).getBlock().setType(team.getBannerItem());
        if (player != null) {
            removeFlag(player, team);
            match.getMessageManager().broadcastMessage(getMM().deserialize(Message.PLAYER_PLACE_FLAG.get().replace("%player%", player.getName())));
        } else {
            match.getMessageManager().broadcastMessage(getMM().deserialize(Message.FLAG_WAS_SAVED.get().replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        }
    }

    /**
     * Makes it so the given player has returned the capturedTeam's flag to scoringTeam
     *
     * @param player       The player who returned the flag
     * @param scoringTeam  The team that returned the flag
     * @param capturedTeam The team who had their flag stolen
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    private void captureFlagAndScore(Player player, Team scoringTeam, Team capturedTeam) {
        Team.requirePlayableTeam(scoringTeam);
        Team.requirePlayableTeam(capturedTeam);
        match.getScoreManager().setScore(scoringTeam, match.getScoreManager().getScore(scoringTeam) + 1);
        match.initPlayers(Team.RED);
        match.initPlayers(Team.BLUE);
        match.getFlagManager().loadFlags(true);
        match.getMessageManager().broadcastMessage(getMM().deserialize(
                Message.PLAYER_RETURN_FLAG.get()
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
        Team.requirePlayableTeam(team);
        String locString = "X: " + (int) location.getX() + " | Y: " + (int) location.getY() + " | Z: " + (int) location.getZ();
        Component component = getMM().deserialize(Message.FLAG_DROPPED_AT.get()
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
        Team.requirePlayableTeam(team);
        return flagLocations.get(team).clone();
    }

    /**
     * Sets the flag location for the given team
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void setFlagLocation(Team team, @NotNull Location newLocation) {
        Team.requirePlayableTeam(team);
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

    public void protectFlagItemEntity(Item item) {
        Team itemTeam = Team.getTeamFromFlag(item.getItemStack());
        if (!Team.playableTeams().contains(itemTeam)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (SimpleCTF.getCurrentMatch() == null || item.isDead()) {
                    this.cancel();
                    item.remove();
                    return;
                }
                if (isItemUnsafe(item)) {
                    item.remove();
                    match.getFlagManager().restoreFlag(null, itemTeam);
                }
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 10);
    }

    private boolean isItemUnsafe(Item item) {
        double safeHeight = (item.getWorld().getMinHeight() + 4);
        return item.getTicksLived() > 20 * 30 || item.getLocation().getY() < safeHeight;
    }
}
