package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CTFJoin {
    private static Set<UUID> redPlayersQueue = new HashSet<>();
    private static Set<UUID> bluePlayersQueue = new HashSet<>();

    public CTFJoin(Player player, String[] args) {
        if (args[1] == null) {
            sendHelpMessage(player);
            return;
        }
        if (!args[1].equalsIgnoreCase("RED") && !args[1].equalsIgnoreCase("BLUE")) {
            sendHelpMessage(player);
            return;
        }
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean matchOccupied = match != null;
        boolean isAlreadyInQueue = redPlayersQueue.contains(player.getUniqueId()) || bluePlayersQueue.contains(player.getUniqueId());

        if (isAlreadyInQueue) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.ALREADY_IN_QUEUE));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.MATCH_OCCUPIED));
            return;
        }

        prepareTeams(player, args[1].toUpperCase());
    }

    private void prepareTeams(Player player, String arg) {
        if (arg.equalsIgnoreCase("RED")) {
            if (redPlayersQueue.size() < ConfigManager.MAX_PLAYERS_PER_TEAM) {
                redPlayersQueue.add(player.getUniqueId());
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.TEAM_ALREADY_FULL));
                return;
            }
        } else if (arg.equalsIgnoreCase("BLUE")) {
            if (bluePlayersQueue.size() < ConfigManager.MAX_PLAYERS_PER_TEAM) {
                bluePlayersQueue.add(player.getUniqueId());
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.TEAM_ALREADY_FULL));
                return;
            }
        } else return;

        redPlayersQueue.forEach(queuePlayer -> {
            Bukkit.getPlayer(queuePlayer).sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_JOINED_TEAM.replaceAll("%player%", player.getName()).replaceAll("%color%", arg)));
        });
        bluePlayersQueue.forEach(queuePlayer -> {
            Bukkit.getPlayer(queuePlayer).sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_JOINED_TEAM.replaceAll("%player%", player.getName()).replaceAll("%color%", arg)));
        });
        player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.TEAM_JOIN.replaceAll("%color%", arg)));
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid command syntax! Correct usage: /ctf join <red|blue>"));
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
}
