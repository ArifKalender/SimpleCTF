package me.kugelbltz.simpleCTF.game;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.UtilizationMethods;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;
import static me.kugelbltz.simpleCTF.util.UtilizationMethods.removeFlag;

// TODO: Handle leave/death situations properly
// TODO: Add sound effects!
// FIXME: Particles can be messed up
public class Match {
    private final Set<Player> redPlayers = new HashSet<>();
    private final Set<Player> bluePlayers = new HashSet<>();
    private final int WIN_SCORE = 3;
    private Location redFlagLocation, blueFlagLocation;
    private Entity redFlagCarrier, blueFlagCarrier;
    private int redScore, blueScore;
    private BossBar bossBar;
    private BukkitTask task;

    public Match(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        boolean canStart = initMatch(redPlayers, bluePlayers);
        if (canStart) this.task = gameLoop();
    }

    /**
     * Initialize and reset match state
     * Returns true if successful, false if not
     */
    private boolean initMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        this.redFlagLocation = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.RedFlag");
        this.blueFlagLocation = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.BlueFlag");
        this.redPlayers.addAll(redPlayers);
        this.bluePlayers.addAll(bluePlayers);
        if (redFlagLocation == null || blueFlagLocation == null) {
            SimpleCTF.getInstance().getLogger().severe("\"Match.Locations.RedFlag\" or \"Match.Locations.BlueFlag was improper or empty. Use /ctf setflag <red|blue> to set locations.");
            broadcastMessage(MM.deserialize("<red> Match environment was not set properly, therefore your match couldn't start. Missing flag locations?"));
            return false;
        }
        this.redScore = 0;
        this.blueScore = 0;
        this.loadBlocks(true);
        SimpleCTF.getInstance().setCurrentMatch(this);
        initPlayers(true);
        return true;
    }

    /**
     * Teleport players to their spawn locations and
     * reset their state if {@code resetState} is true
     */
    private void initPlayers(boolean resetState) {
        redPlayers.forEach(player -> {
            player.teleport(redFlagLocation);
            if (resetState) resetPlayerState(player);
        });
        bluePlayers.forEach(player -> {
            player.teleport(blueFlagLocation);
            if (resetState) resetPlayerState(player);
        });
    }

    /**
     * Handle game loop
     */
    private BukkitTask gameLoop() {
        return new BukkitRunnable() {
            int timeLeft = ConfigManager.MATCH_TIME;

            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0) {
                    unloadMatch(ConfigManager.MATCH_TIME_OUT);
                    this.cancel();
                }
                handleBlue();
                handleRed();
                if (Match.this.blueScore >= Match.this.WIN_SCORE) winMatch(Team.BLUE);
                else if (Match.this.redScore >= Match.this.WIN_SCORE) winMatch(Team.RED);
                updateBossBar(timeLeft);
                playFlagAnimation();
                if (getPlayersInMatch() <= 0) unloadMatch("<red>No players left.");
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 20);
    }

    /**
     * Plays animations of the flags and flag carriers
     */
    private void playFlagAnimation() {
        // --- Block particles for flags ---
        boolean redAvailable = this.redFlagLocation.getBlock().getType() == Material.RED_BANNER;
        boolean blueAvailable = this.blueFlagLocation.getBlock().getType() == Material.BLUE_BANNER;
        if (redAvailable) this.redFlagLocation.getWorld().spawnParticle(Particle.FALLING_DUST, redFlagLocation, 10, 1.5, 1.5, 1.5, Material.RED_CONCRETE_POWDER.createBlockData());
        else this.redFlagLocation.getWorld().spawnParticle(Particle.CRIT, redFlagLocation, 10, 0.5, 0.5, 0.5, 0.05);
        if (blueAvailable) this.blueFlagLocation.getWorld().spawnParticle(Particle.FALLING_DUST, blueFlagLocation, 10, 1.5, 1.5, 1.5, Material.BLUE_CONCRETE_POWDER.createBlockData());
        else this.blueFlagLocation.getWorld().spawnParticle(Particle.CRIT, blueFlagLocation, 10, 0.5, 0.5, 0.5, 0.05);

        // --- Player particles for flag carriers ---
        boolean redCarrierAvailable = getFlagCarrier(Team.RED) != null;
        boolean blueCarrierAvailable = getFlagCarrier(Team.BLUE) != null;
        if (redCarrierAvailable) {
            redFlagCarrier.getWorld().spawnParticle(Particle.FALLING_DUST, redFlagCarrier.getLocation(), 10, 1, 1, 1, Material.RED_CONCRETE_POWDER.createBlockData());
        }
        if (blueCarrierAvailable) {
            blueFlagCarrier.getWorld().spawnParticle(Particle.FALLING_DUST, blueFlagCarrier.getLocation(), 10, 1, 1, 1, Material.BLUE_CONCRETE_POWDER.createBlockData());
        }
    }

    /**
     * If {@code place} is {@code true}, then it places the BANNER blocks. If else, places AIR blocks.
     * @param place Whether to place the banners
     */
    private void loadBlocks(boolean place) {
        if (place) {
            this.redFlagLocation.getBlock().setType(Material.RED_BANNER);
            this.blueFlagLocation.getBlock().setType(Material.BLUE_BANNER);
        } else {
            this.redFlagLocation.getBlock().setType(Material.AIR);
            this.blueFlagLocation.getBlock().setType(Material.AIR);
        }
    }

    /**
     * Handles the blue flag and nearby entities to it
     */
    private void handleBlue() {
        for (LivingEntity entity : blueFlagLocation.getNearbyLivingEntities(3)) {
            if (!(entity instanceof Player player)) continue;
            Team team = Team.getTeam(player);
            if (team == Team.NONE) continue;

            if (team == Team.BLUE) {
                if (blueFlagCarrier != null && blueFlagCarrier.equals(player)) {
                    saveOwnFlag(player, blueFlagLocation, Material.BLUE_BANNER, team);
                    continue;
                }
                if (redFlagCarrier != null && redFlagCarrier.equals(player)) {
                    returnFlag(player, team, Team.getOpposite(team));
                }
            }
        }
    }

    /**
     * Handles the red flag and nearby entities to it
     */
    private void handleRed() {
        for (LivingEntity entity : redFlagLocation.getNearbyLivingEntities(3)) {
            if (!(entity instanceof Player player)) continue;
            Team team = Team.getTeam(player);
            if (team == Team.NONE) continue;
            if (team == Team.RED) {
                if (redFlagCarrier != null && redFlagCarrier.equals(player)) {
                    saveOwnFlag(player, redFlagLocation, Material.RED_BANNER, team);
                    continue;
                }
                if (blueFlagCarrier != null && blueFlagCarrier.equals(player)) {
                    returnFlag(player, team, Team.getOpposite(team));
                }
            }
        }
    }

    /**
     * Saves the flag for the given player, given team and given location with the given material
     */
    private void saveOwnFlag(Player player, Location flagLoc, Material bannerType, Team team) {
        flagLoc.getBlock().setType(bannerType);
        removeFlag(player, team);
        broadcastMessage(MM.deserialize(ConfigManager.PLAYER_PLACE_FLAG.replace("%player%", player.getName())));
    }

    /**
     * Makes it so the player returns the enemy team's flag to their own base
     */
    private void returnFlag(Player player, Team scoringTeam, Team capturedTeam) {
        if (scoringTeam == Team.RED) this.redScore++;
        else this.blueScore++;

        initPlayers(false);
        loadBlocks(true);
        removeFlag(player, capturedTeam);
        broadcastMessage(MM.deserialize(
                ConfigManager.PLAYER_RETURN_FLAG
                        .replace("%player%", player.getName())
                        .replace("%opposite_color%", capturedTeam.name())
        ));
        Bukkit.getPluginManager().callEvent(new FlagScoreEvent(player, scoringTeam, capturedTeam));
    }

    /**
     * Updates the bossbar
     * @param timeLeft To update the health left of the boss bar
     */
    private void updateBossBar(int timeLeft) {
        String title = "Red score: " + this.redScore + " | Blue score: " + this.blueScore;
        double timeLeftNormalized = timeLeft / (double) ConfigManager.MATCH_TIME;
        if (this.bossBar == null) this.bossBar = Bukkit.createBossBar(title, BarColor.YELLOW, BarStyle.SOLID);
        else this.bossBar.setTitle(title);
        this.bossBar.setProgress(timeLeftNormalized);
        redPlayers.forEach(this.bossBar::addPlayer);
        bluePlayers.forEach(this.bossBar::addPlayer);
    }

    /**
     * Unloads match for the given reason
     * @param reason The message to send the players, internally handled via {@code MiniMessage} API
     */
    public void unloadMatch(@Nullable String reason) {
        broadcastMessage(MM.deserialize(reason));
        this.redPlayers.forEach(this::removePlayerFromMatch);
        this.bluePlayers.forEach(this::removePlayerFromMatch);
        this.redScore = 0;
        this.blueScore = 0;
        this.bossBar.removeAll();
        this.bossBar = null;
        this.loadBlocks(false);
        this.task.cancel();
        SimpleCTF.getInstance().setCurrentMatch(null);
    }

    /**
     * Resets the given player's state for the following: Experience, Level, Food level, Health, Inventory, Potions
     * @param player To reset
     */
    public void resetPlayerState(Player player) {
        player.setExp(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    /**
     * Makes it so the given team wins the match.
     */
    public void winMatch(Team team) {
        unloadMatch(ConfigManager.MATCH_WIN.replaceAll("%color%", team.name().toUpperCase(Locale.ENGLISH)));
        Bukkit.getPluginManager().callEvent(new MatchWinEvent(getTeamPlayers(team), getTeamPlayers(Team.getOpposite(team))));
        UtilizationMethods.playSoundForGroup(getTeamPlayers(team), Sound.ITEM_GOAT_HORN_SOUND_1, 3F, 1F);
        UtilizationMethods.playSoundForGroup(getTeamPlayers(Team.getOpposite(team)), Sound.ENTITY_WITHER_AMBIENT, 3F, 1F);
    }

    /**
     * Returns the flag carrier for the given team
     */
    public Entity getFlagCarrier(Team flagColor) {
        if (flagColor == Team.RED) return this.redFlagCarrier;
        else if (flagColor == Team.BLUE) return this.blueFlagCarrier;
        else return null;
    }

    /**
     * Sets the flag carrier for the given team
     */
    public void setFlagCarrier(@Nullable Entity entity, @NotNull Team flagColor) {
        if (flagColor == Team.RED) this.redFlagCarrier = entity;
        else if (flagColor == Team.BLUE) this.blueFlagCarrier = entity;
        else return;
    }

    /**
     * @return The players in the given team
     */
    public Set<Player> getTeamPlayers(Team team) {
        if (team == Team.RED) return this.redPlayers;
        else if (team == Team.BLUE) return this.bluePlayers;
        else return null;
    }

    /**
     * Broadcasts the given component to the players in the match
     * @param component Message to send
     */
    public void broadcastMessage(Component component) {
        redPlayers.forEach(player -> {
            player.sendMessage(component);
        });
        bluePlayers.forEach(player -> {
            player.sendMessage(component);
        });
    }

    /**
     * @return Whether the given player is in a match or not
     */
    public boolean isPlayerInMatch(Player player) {
        return redPlayers.contains(player) || bluePlayers.contains(player);
    }

    /**
     * @return The score of the blue team
     */
    public int getBlueScore() {
        return blueScore;
    }

    /**
     *
     * @return The score of the red team
     */
    public int getRedScore() {
        return redScore;
    }

    /**
     * The amount of players in the game
     */
    public long getPlayersInMatch() {
        return this.redPlayers.size() + this.bluePlayers.size();
    }

    /**
     * Removes the given player from the match
     */
    public void removePlayerFromMatch(Player player) {
        this.redPlayers.remove(player);
        this.bluePlayers.remove(player);
        this.bossBar.removePlayer(player);
        player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
        resetPlayerState(player);
    }

    /**
     * @return The flag location for the given team
     */
    public Location getFlagLocation(Team team) {
        if (team == Team.RED) return this.redFlagLocation.clone();
        else if (team == Team.BLUE) return this.blueFlagLocation.clone();
        else return null;
    }

    /**
     * Broadcasts the dropped flag's location.
     * @param team The team of the flag
     * @param dropper The one who dropped the flag
     * @param location The location at which the flag was dropped
     */
    public void broadcastFlagDropLocation(Team team, Player dropper, Location location) {
        String locString = "X: " + (int) location.getX() + " | Y: " + (int) location.getY() + " | Z: " + (int) location.getZ();
        Component component = MM.deserialize(ConfigManager.FLAG_DROPPED_AT
                .replace("%player%", dropper.getName())
                .replace("%color%", team.name().toUpperCase())
                .replace("%coordinates%", locString));
        broadcastMessage(component);
    }
}
