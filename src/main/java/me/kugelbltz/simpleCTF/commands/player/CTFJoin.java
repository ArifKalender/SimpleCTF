package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFJoin {
    private static Set<UUID> redPlayersQueue = new HashSet<>();
    private static Set<UUID> bluePlayersQueue = new HashSet<>();

    /**
     * Command for players to join a team
     */
    public void execute(Player player, String[] args) {
        if (args.length == 1 || args[1] == null) {
            sendHelpMessage(player);
            return;
        }

        Team team;
        try {
            team = Team.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            if (team == Team.NONE) {
                player.sendMessage(MM.deserialize(StaticVariables.INCORRECT_SYNTAX));
                return;
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(MM.deserialize(StaticVariables.INCORRECT_SYNTAX));
            return;
        }

        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean matchOccupied = match != null;

        if (alreadyInQueue(player)) {
            player.sendMessage(MM.deserialize(StaticVariables.ALREADY_IN_QUEUE));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(MM.deserialize(StaticVariables.MATCH_OCCUPIED));
            return;
        }

        prepareTeams(player, team);
    }

    /**
     * @param team To get the list of
     * @return The list of UUID's of players in a team's queue
     */
    public static Set<UUID> getUUIDQueue(Team team) {
        if (team == Team.RED) return redPlayersQueue;
        else if (team == Team.BLUE) return bluePlayersQueue;
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

    private void prepareTeams(Player player, Team team) {
        if (getPlayerQueue(team).size() < StaticVariables.MAX_PLAYERS_PER_TEAM) {
            addPlayerToQueue(player, team);
        } else {
            player.sendMessage(MM.deserialize(StaticVariables.TEAM_ALREADY_FULL));
            return;
        }

        broadcastMessageToQueue(MM.deserialize(StaticVariables.PLAYER_JOINED_TEAM.replace("%player%", player.getName()).replace("%color%", team.name().toUpperCase(Locale.ENGLISH))));
        player.sendMessage(MM.deserialize(StaticVariables.TEAM_JOIN.replaceAll("%color%", team.name().toLowerCase(Locale.ENGLISH))));
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(MM.deserialize("<red>Invalid command syntax! Correct usage: /ctf join <red|blue>"));
    }

    /**
     * Adds the given player to the given team's queue
     * @param player To add
     * @param team To add to
     */
    public static void addPlayerToQueue(Player player, Team team) {
        UUID uuid = player.getUniqueId();
        if (team == Team.RED) redPlayersQueue.add(uuid);
        else if (team == Team.BLUE) bluePlayersQueue.add(uuid);
        else return;
    }

    /**
     * @return Whether the given player is in a queue or not.
     */
    public static boolean alreadyInQueue(Player player) {
        return redPlayersQueue.contains(player.getUniqueId()) || bluePlayersQueue.contains(player.getUniqueId());
    }

    /**
     * @return The queue {@code Team} of the given player
     */
    public static Team getQueueTeam(Player player) {
        if (redPlayersQueue.contains(player.getUniqueId())) return Team.RED;
        else if (bluePlayersQueue.contains(player.getUniqueId())) return Team.BLUE;
        else return Team.NONE;
    }
}
