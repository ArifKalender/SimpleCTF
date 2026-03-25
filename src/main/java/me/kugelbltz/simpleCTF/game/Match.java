package me.kugelbltz.simpleCTF.game;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.game.managers.FlagManager;
import me.kugelbltz.simpleCTF.game.managers.MessageManager;
import me.kugelbltz.simpleCTF.game.managers.ScoreManager;
import me.kugelbltz.simpleCTF.game.managers.StateManager;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.kugelbltz.simpleCTF.configuration.StaticVariables.getWinScore;

/**
 * Manages general match lifecycle. Call {@link Match#startMatch} to start the match. Supports one match per server instance.
 */
public class Match {
    private final Map<Player, Team> players = new HashMap<>();
    private BukkitTask task;
    private ScoreManager scoreManager;
    private MessageManager messageManager;
    private FlagManager flagManager;
    private StateManager stateManager;

    public void startMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        initManagers();
        boolean canStart = initMatch(redPlayers, bluePlayers);
        if (canStart) this.task = gameLoop();
    }

    private void initManagers() {
        this.scoreManager = new ScoreManager();
        this.stateManager = new StateManager();
        this.messageManager = new MessageManager(this);
        this.flagManager = new FlagManager(this);
    }

    /**
     * Initialize and reset match state, returns true if successful, false if not.
     * Populates teams, prepares flag locations and blocks, sets scores and the boss, initializes players.
     */
    private boolean initMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        redPlayers.forEach(player -> players.put(player, Team.RED));
        bluePlayers.forEach(player -> players.put(player, Team.BLUE));
        if (!getFlagManager().prepareLocations()) return false;
        getFlagManager().loadFlags(true);
        getMessageManager().createBossBar();
        getScoreManager().resetScores();
        initAllPlayers(true, true);
        getMessageManager().broadcastMessage(SimpleCTF.getInstance().getMM().deserialize(Message.MATCH_START.get()));
        SimpleCTF.getInstance().setCurrentMatch(this);
        return true;
    }

    /**
     * Teleport players to their spawn locations and
     * reset their state if {@code resetState} is true
     */
    public void initPlayer(Player player, boolean teleport, boolean resetState) {
        if (getTeam(player) == null) return;
        Team team = getTeam(player);
        getFlagManager().setFlagCarrier(null, team);
        if (teleport) player.teleport(getFlagManager().getFlagLocation(team));
        if (resetState) getStateManager().resetPlayerState(player, true, true, this);
    }

    /**
     * Initializes all players. See {@link Match#initPlayer}
     */
    public void initAllPlayers(boolean teleport, boolean resetState) {
        for (Player player : players.keySet()) {
            initPlayer(player, teleport, resetState);
        }
    }

    /**
     * Handle game loop, 20 tick intervals
     * Handles flags of teams, animations, tracks scores and updates boss bar.
     */
    private BukkitTask gameLoop() {
        return new BukkitRunnable() {
            int timeLeft = StaticVariables.getMatchTime();

            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0 || SimpleCTF.getInstance().getCurrentMatch() == null) {
                    unloadMatch(Message.MATCH_TIME_OUT.get());
                    return;
                }
                for (Team team : Team.playableTeams()) {
                    getFlagManager().handleFlag(team);
                    getFlagManager().playFlagAnimation(team);
                    if (getScoreManager().getScore(team) >= getWinScore()) winMatch(team);
                }

                getMessageManager().updateBossBar(timeLeft);
                if (players.isEmpty()) unloadMatch("<red>No players left.");
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 20);
    }


    /**
     * Unloads match for the given reason
     *
     * @param reason The message to send the players, internally handled via {@code MiniMessage} API
     */
    public void unloadMatch(@Nullable String reason) {
        if (reason != null) getMessageManager().broadcastMessage(SimpleCTF.getInstance().getMM().deserialize(reason));
        removeAllPlayersFromMatch();
        getScoreManager().resetScores();
        getFlagManager().loadFlags(false);
        getMessageManager().unloadBossBar();
        if (this.task != null) this.task.cancel();
        SimpleCTF.getInstance().setCurrentMatch(null);
    }


    public void winMatch(Team team) {
        if (team == null) return;
        Bukkit.getPluginManager().callEvent(new MatchWinEvent(getPlayers(team), getPlayers(Team.getOpposite(team))));
        unloadMatch(Message.MATCH_WIN.get().replace("%color%", team.name().toUpperCase(Locale.ENGLISH)));
    }


    /**
     * @return Whether the given player is in a match or not
     */
    public boolean isPlayerInMatch(Player player) {
        return players.containsKey(player);
    }


    /**
     * Returns the list of players for the given team.
     *
     * @return An unmodifiable Collection of the players for the given team.
     */
    public Collection<Player> getPlayers(Team team) {
        if (team == null) return List.of();
        Collection<Player> toReturn = new HashSet<>();
        players.keySet().forEach(player -> {
            if (players.get(player) == team) toReturn.add(player);
        });
        return toReturn;
    }

    /**
     * Removes the given player from the match.
     */
    public void removePlayerFromMatch(Player player) {
        GeneralUtils.dropAllFlags(player);
        getStateManager().resetPlayerState(player, false, true, this);
        getMessageManager().removePlayerFromBossBar(player);
        player.teleport(StaticVariables.getSpawn());
        if (getTeam(player) == null) return;
        players.remove(player);
    }

    /**
     * Removes all the players from the match
     */
    public void removeAllPlayersFromMatch() {
        new ArrayList<>(players.keySet()).forEach(this::removePlayerFromMatch);
    }

    /**
     * Managers team scores
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Manages bossbar and broadcasts
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Manages flags, banner blocks, returning logic
     */
    public FlagManager getFlagManager() {
        return flagManager;
    }

    /**
     * Manages player inventory, player status effects etc.
     */
    public StateManager getStateManager() {
        return stateManager;
    }

    /**
     * @return The team of the given player
     */
    public Team getTeam(Player player) {
        return players.get(player);
    }
}
