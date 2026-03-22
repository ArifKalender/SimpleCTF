package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;


public class QueueHandler {

    private Collection<UUID> RED_UUID_QUEUE = new HashSet<>();
    private Collection<UUID> BLUE_UUID_QUEUE = new HashSet<>();

    /**
     * @param team To get the list of
     * @return The list of UUID's of players in a team's queue
     */
    private Collection<UUID> getUUIDQueue(Team team) {
        if (team == Team.RED) return RED_UUID_QUEUE;
        else if (team == Team.BLUE) return BLUE_UUID_QUEUE;
        else throw new IllegalArgumentException("team can only be Team.RED or Team.BLUE");
    }

    /**
     * @apiNote
     * @return A copy of the list of players in a teams queue, pulls from {@link QueueHandler#getUUIDQueue(Team)}
     */
    public Set<Player> getPlayerQueue(Team team) {
        Set<Player> toReturn = new HashSet<>();
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
        getPlayerQueue(Team.RED).forEach(player -> {
            player.sendMessage(component);
        });
        getPlayerQueue(Team.BLUE).forEach(player -> {
            player.sendMessage(component);
        });
    }

    /**
     * Properly adds the given player to the queue for the given team
     * @return true if successful, false if else
     */
    public boolean addToQueue(Player player, Team team) {
        if (getPlayerQueue(team).size() < StaticVariables.MAX_PLAYERS_PER_TEAM) {
            addPlayerToQueue(player, team);
        } else {
            player.sendMessage(getMM().deserialize(StaticVariables.TEAM_ALREADY_FULL));
            return false;
        }

        broadcastMessageToQueue(getMM().deserialize(StaticVariables.PLAYER_JOINED_TEAM.replace("%player%", player.getName()).replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        player.sendMessage(getMM().deserialize(StaticVariables.TEAM_JOIN.replace("%color%", team.name().toLowerCase(Locale.ENGLISH))));
        return true;
    }

    /**
     * Adds the given player to the given team's queue directly
     */
    private void addPlayerToQueue(Player player, Team team) {
        UUID uuid = player.getUniqueId();
        if (team == Team.RED) RED_UUID_QUEUE.add(uuid);
        else if (team == Team.BLUE) BLUE_UUID_QUEUE.add(uuid);
        else return;
    }

    public void removePlayerFromQueue(Player player) {
        removePlayerFromQueue(player.getUniqueId());
    }

    public void removePlayerFromQueue(UUID uuid) {
        getUUIDQueue(Team.RED).remove(uuid);
        getUUIDQueue(Team.BLUE).remove(uuid);
    }

    /**
     * @return Whether the given player is in a queue or not.
     */
    public boolean alreadyInQueue(Player player) {
        return RED_UUID_QUEUE.contains(player.getUniqueId()) || BLUE_UUID_QUEUE.contains(player.getUniqueId());
    }

    /** @return Whether anyone is in either of the queues */
    public boolean anyoneInQueue() {
        return !getUUIDQueue(Team.BLUE).isEmpty() || !getUUIDQueue(Team.RED).isEmpty();
    }

    /**
     * @return The queue {@link Team} of the given player
     */
    public Team getQueueTeam(Player player) {
        if (RED_UUID_QUEUE.contains(player.getUniqueId())) return Team.RED;
        else if (BLUE_UUID_QUEUE.contains(player.getUniqueId())) return Team.BLUE;
        else return Team.NONE;
    }

    public void clearQueue() {
        getUUIDQueue(Team.RED).clear();
        getUUIDQueue(Team.BLUE).clear();
    }

    /**
     * Removes the player from the queue.
     * @param player to remove
     * @param sendMessageToPlayer Whether to try to send the player the leaving message
     */
    public void removePlayer(Player player, boolean sendMessageToPlayer) {
        if (!this.alreadyInQueue(player) && Team.getTeam(player) == Team.NONE) {
            if (sendMessageToPlayer)
                player.sendMessage(getMM().deserialize(StaticVariables.NOT_IN_TEAM));
            return;
        }
        Team team = this.getQueueTeam(player);
        if (team == Team.NONE) return;
        this.removePlayerFromQueue(player);
        this.getPlayerQueue(team).forEach(teamPlayer -> teamPlayer
                .sendMessage(getMM().deserialize(StaticVariables.PLAYER_LEFT_TEAM.replace("%player%", player.getName()))));
        if (sendMessageToPlayer)
            player.sendMessage(getMM().deserialize(StaticVariables.TEAM_LEAVE));
    }

}
