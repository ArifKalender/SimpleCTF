package me.kugelbltz.simpleCTF.game;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.game.managers.FlagManager;
import me.kugelbltz.simpleCTF.game.managers.MessageManager;
import me.kugelbltz.simpleCTF.game.managers.ScoreManager;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;

import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.configuration.StaticVariables.getWinScore;

public class Match {
    private final Map<Team, Collection<Player>> players = new HashMap<>();
    private BukkitTask task;

    private ScoreManager scoreManager;
    private MessageManager messageManager;
    private FlagManager flagManager;

    public void startMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        initManagers();
        boolean canStart = initMatch(redPlayers, bluePlayers);
        if (canStart) this.task = gameLoop();
    }

    private void initManagers() {
        this.scoreManager = new ScoreManager();
        this.messageManager = new MessageManager(this);
        this.flagManager = new FlagManager(this);
    }

    /**
     * Initialize and reset match state, returns true if successful, false if not
     */
    private boolean initMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        Location configRed = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.RedFlag");
        Location configBlue = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.BlueFlag");
        if (configRed == null || configBlue == null) {
            SimpleCTF.getInstance().getLogger().severe("\"Match.Locations.RedFlag\" or \"Match.Locations.BlueFlag was improper or empty. Use /ctf setflag <red|blue> to set locations.");
            getMessageManager().broadcastMessage(getMM().deserialize("<red> Match environment was not set properly, therefore your match couldn't start. Missing flag locations?"));
            return false;
        }

        getFlagManager().setFlagLocation(Team.RED, configRed);
        getFlagManager().setFlagLocation(Team.BLUE, configBlue);
        players.put(Team.RED, redPlayers);
        players.put(Team.BLUE, bluePlayers);
        getFlagManager().loadFlags(true);
        getMessageManager().createBossBar();
        for (Team team : Team.playableTeams()) {
            getScoreManager().setScore(team, 0);
            initPlayers(team);
        }
        getMessageManager().broadcastMessage(getMM().deserialize(Message.MATCH_START.get()));
        SimpleCTF.getInstance().setCurrentMatch(this);
        return true;
    }

    /**
     * Teleport players to their spawn locations and
     * reset their state if {@code resetState} is true
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void initPlayers(Team team) {
        Team.requirePlayableTeam(team);
        getFlagManager().setFlagCarrier(null, team);
        getPlayers(team).forEach(player -> {
            player.teleport(getFlagManager().getFlagLocation(team));
            resetPlayerState(player);
        });
    }

    /**
     * Handle game loop, 20 tick intervals
     */
    private BukkitTask gameLoop() {
        return new BukkitRunnable() {
            int timeLeft = StaticVariables.getMatchTime();

            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0 || SimpleCTF.getCurrentMatch() == null) {
                    unloadMatch(Message.MATCH_TIME_OUT.get());
                    return;
                }
                for (Team team : Team.playableTeams()) {
                    getFlagManager().handleFlag(team);
                    getFlagManager().playFlagAnimation(team);
                    if (getScoreManager().getScore(team) >= getWinScore()) winMatch(team);
                }

                getMessageManager().updateBossBar(timeLeft);
                int playersInMatch = getPlayers(Team.RED).size() + getPlayers(Team.BLUE).size();
                if (playersInMatch <= 0) unloadMatch("<red>No players left.");
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 20);
    }


    /**
     * Unloads match for the given reason
     *
     * @param reason The message to send the players, internally handled via {@code MiniMessage} API
     */
    public void unloadMatch(@Nullable String reason) {
        if (reason != null) getMessageManager().broadcastMessage(getMM().deserialize(reason));
        for (Team team : Team.playableTeams()) {
            new ArrayList<>(getPlayers(team)).forEach(this::removePlayerFromMatch); // Copy to prevent ConcurrentModificationException
            getScoreManager().setScore(team, 0);
        }
        getFlagManager().loadFlags(false);
        getMessageManager().unloadBossBar();
        if (this.task != null) this.task.cancel();
        SimpleCTF.getInstance().setCurrentMatch(null);
    }

    /**
     * Resets the given player's state for the following: Experience, Level, Food level, Health, Inventory, Potions
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
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void winMatch(Team team) {
        Team.requirePlayableTeam(team);
        Bukkit.getPluginManager().callEvent(new MatchWinEvent(getPlayers(team), getPlayers(Team.getOpposite(team))));
        unloadMatch(Message.MATCH_WIN.get().replace("%color%", team.name().toUpperCase(Locale.ENGLISH)));
    }


    /**
     * @return Whether the given player is in a match or not
     */
    public boolean isPlayerInMatch(Player player) {
        return getPlayers(Team.RED).contains(player) || getPlayers(Team.BLUE).contains(player);
    }


    /**
     * Returns the list of players for the given team.
     *
     * @return An unmodifiable Collection of the players for the given team.
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public Collection<Player> getPlayers(Team team) {
        Team.requirePlayableTeam(team);
        return Collections.unmodifiableCollection(players.get(team));
    }

    /**
     * Removes the given player from the match.
     */
    public void removePlayerFromMatch(Player player) {
        GeneralUtils.dropAllFlags(player);
        resetPlayerState(player);
        getMessageManager().removePlayerFromBossBar(player);
        player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
        players.get(Team.getTeam(player)).remove(player);
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

}
