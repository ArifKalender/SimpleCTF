package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

// FIXME: SimpleCTF » You are not in a team. when in a match (because list resets when /ctf start is used)
public class CTFLeave {
    /**
     * Command for players to leave from a match or their queue
     */
    public CTFLeave(Player player, String[] args) {
        removePlayer(player, true);
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match != null) match.removePlayerFromMatch(player);
    }

    public static void removePlayer(Player player, boolean sendMessageToPlayer) {
        if (!CTFJoin.alreadyInQueue(player) && Team.getTeam(player) == Team.NONE) {
            if (sendMessageToPlayer) player.sendMessage(MM.deserialize(ConfigManager.NOT_IN_TEAM));
            return;
        }
        Team team = CTFJoin.getQueueTeam(player);
        CTFJoin.getUUIDQueue(team).remove(player.getUniqueId());
        CTFJoin.getPlayerQueue(team).forEach(teamPlayer -> teamPlayer.sendMessage(MM.deserialize(ConfigManager.PLAYER_LEFT_TEAM.replace("%player%", player.getName()))));
        if (sendMessageToPlayer) player.sendMessage(MM.deserialize(ConfigManager.TEAM_LEAVE));
    }
}
