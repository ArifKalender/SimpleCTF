package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.commands.player.CTFJoin.*;

public class CTFLeave {
    public CTFLeave(Player player, String[] args) {
        removePlayer(player, true);
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match != null) match.removePlayerFromMatch(player);
    }

    public static void removePlayer(Player player, boolean sendMessageToPlayer) {
        boolean isInQueue = getRedPlayersUUIDQueue().contains(player.getUniqueId()) || getBluePlayersUUIDQueue().contains(player.getUniqueId());
        if (!isInQueue) {
            if (sendMessageToPlayer) player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NOT_IN_TEAM));
            return;
        }

        if (getBluePlayersUUIDQueue().contains(player.getUniqueId()))  {
            getBluePlayersUUIDQueue().remove(player.getUniqueId());
            getBluePlayersUUIDQueue().forEach(queuedPlayer -> {
                Bukkit.getPlayer(queuedPlayer).sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
            });
        } else if (getRedPlayersUUIDQueue().contains(player.getUniqueId())) {
            getRedPlayersUUIDQueue().remove(player.getUniqueId());
            getRedPlayersUUIDQueue().forEach(queuedPlayer -> {
                Bukkit.getPlayer(queuedPlayer).sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
            });
        }
        if (sendMessageToPlayer) player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.TEAM_LEAVE));
    }
}
