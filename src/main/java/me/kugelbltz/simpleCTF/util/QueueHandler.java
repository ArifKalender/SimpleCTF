package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class QueueHandler {

    private static Collection<UUID> RED_UUID_QUEUE = new HashSet<>();
    private static Collection<UUID> BLUE_UUID_QUEUE = new HashSet<>();

    /**
     * @param team To get the list of
     * @return The list of UUID's of players in a team's queue
     */
    private static Collection<UUID> getUUIDQueue(Team team) {
        if (team == Team.RED) return RED_UUID_QUEUE;
        else if (team == Team.BLUE) return BLUE_UUID_QUEUE;
        else throw new IllegalArgumentException("team can only be Team.RED or Team.BLUE");
    }

    /**
     * @param team To get the list of
     * @apiNote Read only
     * @return The list of players in a teams queue, pulls from {@code getUUIDQueue()}
     */
    public static Set<Player> getPlayerQueue(Team team) {
        Set<Player> toReturn = new HashSet<>();
        getUUIDQueue(team).forEach(uuid -> {
            Player toAdd = Bukkit.getPlayer(uuid);
            if (toAdd != null) toReturn.add(toAdd);
        });
        return toReturn;
    }

    /**
     * Broadcast the given message to all queued players
     * @param component
     */
    public static void broadcastMessageToQueue(Component component) {
        getPlayerQueue(Team.RED).forEach(player -> {
            player.sendMessage(component);
        });
        getPlayerQueue(Team.BLUE).forEach(player -> {
            player.sendMessage(component);
        });
    }

    public static void prepareTeams(Player player, Team team) {
        if (getPlayerQueue(team).size() < StaticVariables.MAX_PLAYERS_PER_TEAM) {
            addPlayerToQueue(player, team);
        } else {
            player.sendMessage(MM.deserialize(StaticVariables.TEAM_ALREADY_FULL));
            return;
        }

        broadcastMessageToQueue(MM.deserialize(StaticVariables.PLAYER_JOINED_TEAM.replace("%player%", player.getName()).replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        player.sendMessage(MM.deserialize(StaticVariables.TEAM_JOIN.replace("%color%", team.name().toLowerCase(Locale.ENGLISH))));
    }

    /**
     * Adds the given player to the given team's queue
     * @param player To add
     * @param team To add to
     */
    public static void addPlayerToQueue(Player player, Team team) {
        UUID uuid = player.getUniqueId();
        if (team == Team.RED) RED_UUID_QUEUE.add(uuid);
        else if (team == Team.BLUE) BLUE_UUID_QUEUE.add(uuid);
        else return;
    }

    public static void removePlayerFromQueue(Player player) {
        removePlayerFromQueue(player.getUniqueId());
    }
    public static void removePlayerFromQueue(UUID uuid) {
        getUUIDQueue(Team.RED).remove(uuid);
        getUUIDQueue(Team.BLUE).remove(uuid);
    }

    /**
     * @return Whether the given player is in a queue or not.
     */
    public static boolean alreadyInQueue(Player player) {
        return RED_UUID_QUEUE.contains(player.getUniqueId()) || BLUE_UUID_QUEUE.contains(player.getUniqueId());
    }

    public static boolean anyoneInQueue() {
        return !getUUIDQueue(Team.BLUE).isEmpty() || !getUUIDQueue(Team.RED).isEmpty();
    }

    /**
     * @return The queue {@code Team} of the given player
     */
    public static Team getQueueTeam(Player player) {
        if (RED_UUID_QUEUE.contains(player.getUniqueId())) return Team.RED;
        else if (BLUE_UUID_QUEUE.contains(player.getUniqueId())) return Team.BLUE;
        else return Team.NONE;
    }

    public static void clearQueue() {
        getUUIDQueue(Team.RED).clear();
        getUUIDQueue(Team.BLUE).clear();
    }

}
