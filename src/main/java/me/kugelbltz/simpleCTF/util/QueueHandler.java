package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.configuration.Message;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;


public class QueueHandler {

    private final Collection<UUID> redUuidQueue = new HashSet<>();
    private final Collection<UUID> blueUuidQueue = new HashSet<>();

    /**
     * @param team To get the list of
     * @return The list of UUID's of players in a team's queue
     */
    private Collection<UUID> getUUIDQueue(Team team) {
        if (team == Team.RED) return redUuidQueue;
        else if (team == Team.BLUE) return blueUuidQueue;
        else throw new IllegalArgumentException("team can only be Team.RED or Team.BLUE");
    }

    /**
     * @return A copy of the list of players in a teams queue, pulls from {@link QueueHandler#getUUIDQueue(Team)}
     * @apiNote
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
     *
     * @return true if successful, false if else
     */
    public boolean addToQueue(Player player, Team team) {
        if (getPlayerQueue(team).size() < StaticVariables.getMaxPlayersPerTeam()) {
            addPlayerToQueue(player, team);
        } else {
            player.sendMessage(getMM().deserialize(Message.TEAM_ALREADY_FULL.get()));
            return false;
        }

        broadcastMessageToQueue(getMM().deserialize(Message.PLAYER_JOINED_TEAM.get().replace("%player%", player.getName()).replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        player.sendMessage(getMM().deserialize(Message.TEAM_JOIN.get().replace("%color%", team.name().toLowerCase(Locale.ENGLISH))));
        return true;
    }

    /**
     * Adds the given player to the given team's queue directly
     *
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    private void addPlayerToQueue(Player player, Team team) {
        UUID uuid = player.getUniqueId();
        if (team == Team.RED) redUuidQueue.add(uuid);
        else if (team == Team.BLUE) blueUuidQueue.add(uuid);
        else throw new IllegalArgumentException("Team NONE is not allowed");
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
        return redUuidQueue.contains(player.getUniqueId()) || blueUuidQueue.contains(player.getUniqueId());
    }

    /**
     * @return Whether anyone is in either of the queues
     */
    public boolean anyoneInQueue() {
        return !getUUIDQueue(Team.BLUE).isEmpty() || !getUUIDQueue(Team.RED).isEmpty();
    }

    /**
     * @return The queue {@link Team} of the given player
     */
    public Team getQueueTeam(Player player) {
        if (redUuidQueue.contains(player.getUniqueId())) return Team.RED;
        else if (blueUuidQueue.contains(player.getUniqueId())) return Team.BLUE;
        else return Team.NONE;
    }

    public void clearQueue() {
        getUUIDQueue(Team.RED).clear();
        getUUIDQueue(Team.BLUE).clear();
    }

    /**
     * Removes the player from the queue.
     *
     * @param player              to remove
     * @param sendMessageToPlayer Whether to try to send the player the leaving message
     */
    public void removePlayer(Player player, boolean sendMessageToPlayer) {
        if (!this.alreadyInQueue(player) && Team.getTeam(player) == Team.NONE) {
            if (sendMessageToPlayer)
                player.sendMessage(getMM().deserialize(Message.NOT_IN_TEAM.get()));
            return;
        }
        Team team = this.getQueueTeam(player);
        if (team == Team.NONE) return;
        this.removePlayerFromQueue(player);
        this.getPlayerQueue(team).forEach(teamPlayer -> teamPlayer
                .sendMessage(getMM().deserialize(Message.PLAYER_LEFT_TEAM.get().replace("%player%", player.getName()))));
        if (sendMessageToPlayer)
            player.sendMessage(getMM().deserialize(Message.TEAM_LEAVE.get()));
    }

}
