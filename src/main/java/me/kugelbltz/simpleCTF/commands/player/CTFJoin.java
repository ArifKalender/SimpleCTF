package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFJoin {
    private static Set<UUID> redPlayersQueue = new HashSet<>();
    private static Set<UUID> bluePlayersQueue = new HashSet<>();

    public CTFJoin(Player player, String[] args) {
        if (args[1] == null) {
            sendHelpMessage(player);
            return;
        }

        Team team = null;
        try {
            team = Team.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            if (team == Team.NONE) {
                player.sendMessage(MM.deserialize(ConfigManager.INCORRECT_SYNTAX));
                return;
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(MM.deserialize(ConfigManager.INCORRECT_SYNTAX));
            return;
        }
        ;

        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean matchOccupied = match != null;
        boolean isAlreadyInQueue = redPlayersQueue.contains(player.getUniqueId()) || bluePlayersQueue.contains(player.getUniqueId());

        if (isAlreadyInQueue) {
            player.sendMessage(MM.deserialize(ConfigManager.ALREADY_IN_QUEUE));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(MM.deserialize(ConfigManager.MATCH_OCCUPIED));
            return;
        }

        prepareTeams(player, team);
    }

    public static Set<UUID> getRedPlayersUUIDQueue() {
        return redPlayersQueue;
    }

    public static Set<UUID> getBluePlayersUUIDQueue() {
        return bluePlayersQueue;
    }

    public static Set<Player> getRedPlayersQueue() {
        Set<Player> toReturn = new HashSet<>();
        getRedPlayersUUIDQueue().forEach(uuid -> toReturn.add(Bukkit.getPlayer(uuid)));
        return toReturn;
    }

    public static Set<Player> getBluePlayersQueue() {
        Set<Player> toReturn = new HashSet<>();
        getBluePlayersUUIDQueue().forEach(uuid -> toReturn.add(Bukkit.getPlayer(uuid)));
        return toReturn;
    }

    public static void broadcastMessageToQueue(Component component) {
        getRedPlayersQueue().forEach(player -> {
            player.sendMessage(component);
        });
        getBluePlayersQueue().forEach(player -> {
            player.sendMessage(component);
        });
    }

    private void prepareTeams(Player player, Team team) {
        if (team == Team.RED) {
            if (redPlayersQueue.size() < ConfigManager.MAX_PLAYERS_PER_TEAM) {
                redPlayersQueue.add(player.getUniqueId());
            } else {
                player.sendMessage(MM.deserialize(ConfigManager.TEAM_ALREADY_FULL));
                return;
            }
        } else if (team == Team.BLUE) {
            if (bluePlayersQueue.size() < ConfigManager.MAX_PLAYERS_PER_TEAM) {
                bluePlayersQueue.add(player.getUniqueId());
            } else {
                player.sendMessage(MM.deserialize(ConfigManager.TEAM_ALREADY_FULL));
                return;
            }
        } else return;

        redPlayersQueue.forEach(queuePlayer -> {
            Bukkit.getPlayer(queuePlayer).sendMessage(MM.deserialize(ConfigManager.PLAYER_JOINED_TEAM.replaceAll("%player%", player.getName()).replaceAll("%color%", team.name().toLowerCase(Locale.ENGLISH))));
        });
        bluePlayersQueue.forEach(queuePlayer -> {
            Bukkit.getPlayer(queuePlayer).sendMessage(MM.deserialize(ConfigManager.PLAYER_JOINED_TEAM.replaceAll("%player%", player.getName()).replaceAll("%color%", team.name().toLowerCase(Locale.ENGLISH))));
        });
        player.sendMessage(MM.deserialize(ConfigManager.TEAM_JOIN.replaceAll("%color%", team.name().toLowerCase(Locale.ENGLISH))));
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(MM.deserialize("<red>Invalid command syntax! Correct usage: /ctf join <red|blue>"));
    }
}
