package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;
import static me.kugelbltz.simpleCTF.commands.player.CTFJoin.getBluePlayersUUIDQueue;
import static me.kugelbltz.simpleCTF.commands.player.CTFJoin.getRedPlayersUUIDQueue;

// FIXME: SimpleCTF » You are not in a team. when in a match (because list resets when /ctf start is used)
public class CTFLeave {
    public CTFLeave(Player player, String[] args) {
        removePlayer(player, true);
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match != null) match.removePlayerFromMatch(player);
    }

    public static void removePlayer(Player player, boolean sendMessageToPlayer) {
        boolean isInQueue = getRedPlayersUUIDQueue().contains(player.getUniqueId()) || getBluePlayersUUIDQueue().contains(player.getUniqueId());
        if (!isInQueue) {
            if (sendMessageToPlayer) player.sendMessage(MM.deserialize(ConfigManager.NOT_IN_TEAM));
            return;
        }

        if (getBluePlayersUUIDQueue().contains(player.getUniqueId())) {
            getBluePlayersUUIDQueue().remove(player.getUniqueId());
            getBluePlayersUUIDQueue().forEach(queuedPlayer -> {
                Bukkit.getPlayer(queuedPlayer).sendMessage(MM.deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
            });
        } else if (getRedPlayersUUIDQueue().contains(player.getUniqueId())) {
            getRedPlayersUUIDQueue().remove(player.getUniqueId());
            getRedPlayersUUIDQueue().forEach(queuedPlayer -> {
                Bukkit.getPlayer(queuedPlayer).sendMessage(MM.deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
            });
        }
        if (sendMessageToPlayer) player.sendMessage(MM.deserialize(ConfigManager.TEAM_LEAVE));
    }
}
