package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handler of match queues
 */
public class QueueHandler {

    private final Map<Team, Collection<UUID>> teamQueues = new HashMap<>();

    public QueueHandler() {
        for (Team team : Team.playableTeams()) teamQueues.put(team, new HashSet<>());
    }

    /**
     * @param team To get the list of
     * @return The unmodifiable list of UUID's of players in a team's queue
     */
    private Collection<UUID> getUUIDQueue(Team team) {
        if (team == null) return List.of();
        return Collections.unmodifiableCollection(teamQueues.get(team));
    }

    /**
     * @return A copy of the list of players in a teams queue, pulls from {@link QueueHandler#getUUIDQueue(Team)}
     */
    public Collection<Player> getPlayerQueue(Team team) {
        Collection<Player> toReturn = new HashSet<>();
        getUUIDQueue(team).forEach(uuid -> {
            Player toAdd = Bukkit.getPlayer(uuid);
            if (toAdd != null) toReturn.add(toAdd);
        });
        return toReturn;
    }

    /**
     * Broadcast the given message to all queued players
     */
    public void broadcastMessageToQueue(Component component) {
        for (Team team : Team.playableTeams()) {
            getPlayerQueue(team).forEach(player -> player.sendMessage(component));
        }
    }

    /**
     * Properly adds the given player to the queue for the given team
     *
     * @return true if successful, false if else
     */
    public boolean addToQueue(Player player, Team team) {
        if (getPlayerQueue(team).size() < StaticVariables.getMaxPlayersPerTeam()) {
            addPlayerToQueue(player, team);
        } else {
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.TEAM_ALREADY_FULL.get()));
            return false;
        }

        broadcastMessageToQueue(SimpleCTF.getInstance().getMM().deserialize(Message.PLAYER_JOINED_TEAM.get().replace("%player%", player.getName()).replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.TEAM_JOIN.get().replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        return true;
    }

    /**
     * Adds the given player to the given team's queue directly
     */
    private void addPlayerToQueue(Player player, Team team) {
        if (team == null) return;
        UUID uuid = player.getUniqueId();
        teamQueues.get(team).add(uuid);
    }

    public void removePlayerFromQueue(Player player) {
        removePlayerFromQueue(player.getUniqueId());
    }

    public void removePlayerFromQueue(UUID uuid) {
        for (Collection<UUID> value : teamQueues.values()) value.remove(uuid);

    }

    /**
     * @return Whether the given player is in a queue or not.
     */
    public boolean isInQueue(Player player) {
        for (Collection<UUID> value : teamQueues.values()) if (value.contains(player.getUniqueId())) return true;
        return false;
    }

    /**
     * @return Whether anyone is in either of the queues
     */
    public boolean anyoneInQueue() {
        for (Collection<UUID> value : teamQueues.values()) if (!value.isEmpty()) return true;
        return false;
    }

    /**
     * @return The queue {@link Team} of the given player
     */
    public Team getQueueTeam(Player player) {
        for (Team team : teamQueues.keySet()) if (teamQueues.get(team).contains(player.getUniqueId())) return team;
        return null;
    }

    public void clearQueue() {
        for (Team team : teamQueues.keySet()) teamQueues.get(team).clear();

    }

    /**
     * Removes the player from the queue.
     *
     * @param player              to remove
     * @param sendMessageToPlayer Whether to try to send the player the leaving message
     */
    public void removePlayer(Player player, boolean sendMessageToPlayer) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match != null) {
            boolean notInTeam = !this.isInQueue(player) && match.getTeam(player) == null;
            if (notInTeam) {
                if (sendMessageToPlayer)
                    player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.NOT_IN_TEAM.get()));
                return;
            }
        }
        Team team = this.getQueueTeam(player);
        if (team == null) return;
        this.removePlayerFromQueue(player);
        this.getPlayerQueue(team).forEach(teamPlayer -> teamPlayer
                .sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PLAYER_LEFT_TEAM.get().replace("%player%", player.getName()))));
        if (sendMessageToPlayer)
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.TEAM_LEAVE.get()));
    }
}
