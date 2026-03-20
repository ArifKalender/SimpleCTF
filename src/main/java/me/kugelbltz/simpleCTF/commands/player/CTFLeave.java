package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.commands.player.CTFJoin.*;

public class CTFLeave {
    public CTFLeave(Player player, String[] args) {
        removePlayer(player, true);
    }

    public static void removePlayer(Player player, boolean sendMessageToPlayer) {
        boolean isInQueue = getRedPlayersQueue().contains(player.getUniqueId()) || getBluePlayersQueue().contains(player.getUniqueId());
        if (!isInQueue) {
            if (sendMessageToPlayer) player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NOT_IN_TEAM));
            return;
        }

        if (getBluePlayersQueue().contains(player.getUniqueId()))  {
            getBluePlayersQueue().remove(player.getUniqueId());
            getBluePlayersQueue().forEach(queuedPlayer -> {
                Bukkit.getPlayer(queuedPlayer).sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
            });
        } else if (getRedPlayersQueue().contains(player.getUniqueId())) {
            getRedPlayersQueue().remove(player.getUniqueId());
            getRedPlayersQueue().forEach(queuedPlayer -> {
                Bukkit.getPlayer(queuedPlayer).sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
            });
        }
    }
}
