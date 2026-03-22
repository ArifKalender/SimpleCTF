package me.kugelbltz.simpleCTF.game;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.UtilizationMethods.removeFlag;

public class Match {
    private final Map<Team, Collection<Player>> players = new HashMap<>();
    private final Map<Team, Location> flagLocations = new HashMap<>();
    private final Map<Team, Entity> flagCarriers = new HashMap<>();
    private final Map<Team, Integer> teamScores = new HashMap<>();
    private static final int WIN_SCORE = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.WinScore", 3);
    private BossBar bossBar;
    private BukkitTask task;

    public void startMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        boolean canStart = initMatch(redPlayers, bluePlayers);
        if (canStart) this.task = gameLoop();
    }

    /**
     * Initialize and reset match state
     * Returns true if successful, false if not
     */
    private boolean initMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        setFlagLocation(Team.RED, SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.RedFlag"));
        setFlagLocation(Team.BLUE, SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.BlueFlag"));
        players.put(Team.RED, redPlayers);
        players.put(Team.BLUE, bluePlayers);
        if (flagLocations.get(Team.RED) == null || flagLocations.get(Team.BLUE) == null) {
            SimpleCTF.getInstance().getLogger().severe("\"Match.Locations.RedFlag\" or \"Match.Locations.BlueFlag was improper or empty. Use /ctf setflag <red|blue> to set locations.");
            broadcastMessage(getMM().deserialize("<red> Match environment was not set properly, therefore your match couldn't start. Missing flag locations?"));
            return false;
        }
        this.loadBlocks(true);
        SimpleCTF.getInstance().setCurrentMatch(this);
        setScore(Team.RED, 0);
        setScore(Team.BLUE, 0);
        initPlayers(Team.RED, true);
        initPlayers(Team.BLUE, true);
        broadcastMessage(getMM().deserialize(StaticVariables.MATCH_START));
        return true;
    }

    /**
     * Teleport players to their spawn locations and
     * reset their state if {@code resetState} is true
     */
    private void initPlayers(Team team, boolean resetState) {
        getPlayers(team).forEach(player -> {
            player.teleport(flagLocations.get(team));
            if (resetState) resetPlayerState(player);
        });
    }

    /**
     * Handle game loop
     */
    private BukkitTask gameLoop() {
        return new BukkitRunnable() {
            int timeLeft = StaticVariables.MATCH_TIME;

            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0) {
                    unloadMatch(StaticVariables.MATCH_TIME_OUT);
                    this.cancel();
                }
                handleFlag(Team.RED, Material.RED_BANNER);
                handleFlag(Team.BLUE, Material.BLUE_BANNER);
                playFlagAnimation(Team.RED, Material.RED_BANNER, Material.RED_CONCRETE);
                playFlagAnimation(Team.BLUE, Material.BLUE_BANNER, Material.BLUE_CONCRETE);

                if (getScore(Team.BLUE) >= WIN_SCORE) winMatch(Team.BLUE);
                else if (getScore(Team.RED) >= WIN_SCORE) winMatch(Team.RED);

                updateBossBar(timeLeft);
                if (getPlayersInMatch() <= 0) unloadMatch("<red>No players left.");
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 20);
    }

    /**
     * Plays animations of the flags and flag carriers
     */
    private void playFlagAnimation(Team team, Material banner, Material particleColorSource) {
        // --- Block particles for flags ---
        Location flagLoc = getFlagLocation(team);
        boolean available = flagLoc.getBlock().getType() == banner;
        if (available) flagLoc.getWorld().spawnParticle(Particle.FALLING_DUST, flagLoc,
                10, 1.5, 1.5, 1.5, particleColorSource.createBlockData());
        else flagLoc.getWorld().spawnParticle(Particle.CRIT, flagLoc,
                10, 0.5, 0.5, 0.5, 0.05);

        // --- Player particles for flag carriers ---
        Entity carrier = getFlagCarrier(team);
        if (carrier != null) {
            carrier.getWorld().spawnParticle(Particle.FALLING_DUST, carrier.getLocation(),
                    10, 1, 1, 1, particleColorSource.createBlockData());
        }
    }

    /**
     * If {@code place} is {@code true}, then it places the BANNER blocks. If else, places AIR blocks.
     *
     * @param place Whether to place the banners
     */
    private void loadBlocks(boolean place) {
        if (place) {
            getFlagLocation(Team.RED).getBlock().setType(Material.RED_BANNER);
            getFlagLocation(Team.BLUE).getBlock().setType(Material.BLUE_BANNER);
        } else {
            getFlagLocation(Team.RED).getBlock().setType(Material.AIR);
            getFlagLocation(Team.BLUE).getBlock().setType(Material.AIR);
        }
    }

    /**
     * Handles the given flag and nearby entities to it
     */
    private void handleFlag(Team flag, Material bannerType) {
        for (LivingEntity lEntity : getFlagLocation(flag).getNearbyLivingEntities(3)) {
            if (!(lEntity instanceof Player player)) continue;
            Team loopPlayerTeam = Team.getTeam(player);
            Team enemyTeam = Team.getOpposite(flag);
            Entity flagCarrierFunc = getFlagCarrier(flag);
            Entity flagCarrierEnemy = getFlagCarrier(enemyTeam);
            Location flagLoc = getFlagLocation(flag);

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
     * Saves the flag for the given player, given team and given location with the given material
     */
    private void saveOwnFlag(Player player, Location flagLoc, Material bannerType, Team team) {
        flagLoc.getBlock().setType(bannerType);
        removeFlag(player, team);
        broadcastMessage(getMM().deserialize(StaticVariables.PLAYER_PLACE_FLAG.replace("%player%", player.getName())));
    }

    /**
     * Makes it so the player returns the enemy team's flag to their own base
     */
    private void returnFlag(Player player, Team scoringTeam, Team capturedTeam) {
        setScore(scoringTeam, getScore(scoringTeam) + 1);

        initPlayers(Team.RED, false);
        initPlayers(Team.BLUE, false);
        loadBlocks(true);
        removeFlag(player, capturedTeam);
        broadcastMessage(getMM().deserialize(
                StaticVariables.PLAYER_RETURN_FLAG
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
        String title = "Red score: " + getScore(Team.RED) + " | Blue score: " + getScore(Team.BLUE);
        double timeLeftNormalized = timeLeft / (double) StaticVariables.MATCH_TIME;
        if (this.bossBar == null) this.bossBar = Bukkit.createBossBar(title, BarColor.YELLOW, BarStyle.SOLID);
        else this.bossBar.setTitle(title);
        this.bossBar.setProgress(timeLeftNormalized);
        players.get(Team.RED).forEach(this.bossBar::addPlayer);
        players.get(Team.BLUE).forEach(this.bossBar::addPlayer);
    }

    /**
     * Unloads match for the given reason
     * @param reason The message to send the players, internally handled via {@code MiniMessage} API
     */
    public void unloadMatch(@Nullable String reason) {
        if (reason != null) broadcastMessage(getMM().deserialize(reason));
        getPlayers(Team.RED).forEach(this::removePlayerFromMatch);
        getPlayers(Team.BLUE).forEach(this::removePlayerFromMatch);
        setScore(Team.RED, 0);
        setScore(Team.BLUE, 0);
        this.loadBlocks(false);
        if (this.task != null) this.task.cancel();
        if (this.bossBar != null) {
            this.bossBar.removeAll();
            this.bossBar=null;
        }
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
        Bukkit.getPluginManager().callEvent(new MatchWinEvent(getPlayers(team), getPlayers(Team.getOpposite(team))));

        unloadMatch(StaticVariables.MATCH_WIN.replace("%color%", team.name().toUpperCase(Locale.ENGLISH)));
    }

    /**
     * Returns the flag carrier for the given team
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

    /**
     * Broadcasts the given component to the players in the match
     *
     * @param component Message to send
     */
    public void broadcastMessage(Component component) {
        getPlayers(Team.RED).forEach(player -> {
            player.sendMessage(component);
        });
        getPlayers(Team.BLUE).forEach(player -> {
            player.sendMessage(component);
        });
    }

    /**
     * @return Whether the given player is in a match or not
     */
    public boolean isPlayerInMatch(Player player) {
        return getPlayers(Team.RED).contains(player) || getPlayers(Team.BLUE).contains(player);
    }

    /**
     * @return The score of the given team
     */
    public int getScore(Team team) {
        return teamScores.get(team);
    }

    public void setScore(Team team, int newScore) {
        teamScores.put(team, newScore);
    }

    /**
     * The amount of players in the game
     */
    public int getPlayersInMatch() {
        return getPlayers(Team.RED).size() + getPlayers(Team.BLUE).size();
    }

    /**
     * Returns the list of players for the given team.
     * @apiNote Read-only
     */
    public Collection<Player> getPlayers(Team team) {
        return new HashSet<>(players.get(team));
    }

    /**
     * Removes the given player from the match
     */
    public void removePlayerFromMatch(Player player) {
        players.get(Team.RED).remove(player); //Not using getPlayers() because it returns a copy where we want to remove the players from the actual list
        players.get(Team.BLUE).remove(player);
        if (this.bossBar != null) this.bossBar.removePlayer(player);
        player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
        resetPlayerState(player);
    }

    /**
     * @return The flag location for the given team
     * @apiNote Read-only
     */
    public Location getFlagLocation(Team team) {
        return flagLocations.get(team).clone();
    }

    /**
     * Sets the flag location for the given team
     */
    public void setFlagLocation(Team team, Location newLocation) {
        flagLocations.put(team, newLocation);
    }

    /**
     * Broadcasts the dropped flag's location.
     *
     * @param team     The team of the flag
     * @param dropper  The one who dropped the flag
     * @param location The location at which the flag was dropped
     */
    public void broadcastFlagDropLocation(Team team, Player dropper, Location location) {
        String locString = "X: " + (int) location.getX() + " | Y: " + (int) location.getY() + " | Z: " + (int) location.getZ();
        Component component = getMM().deserialize(StaticVariables.FLAG_DROPPED_AT
                .replace("%player%", dropper.getName())
                .replace("%color%", team.name().toUpperCase())
                .replace("%coordinates%", locString));
        broadcastMessage(component);
    }
}
